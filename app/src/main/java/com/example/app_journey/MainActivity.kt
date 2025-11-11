package com.example.app_journey

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.Usuario
import com.example.app_journey.screens.*
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import com.example.app_journey.screens.ChatGrupoScreen
import com.example.app_journey.screens.ChatPrivadoScreen
import com.example.app_journey.screens.ConversasPrivadasScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    var usuarioLogado by remember { mutableStateOf<Usuario?>(null) }
    var carregandoUsuario by remember { mutableStateOf(true) }

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Carregar usuário logado do SharedPref
    LaunchedEffect(Unit) {
        val idSalvo = SharedPrefHelper.recuperarIdUsuario(context)
        Log.d("MainActivity", "ID salvo: $idSalvo")

        if (idSalvo != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idSalvo)
                if (!result.usuario.isNullOrEmpty()) {
                    usuarioLogado = result.usuario[0]
                    Log.d("MainActivity", "Usuário carregado: ${usuarioLogado?.nome_completo}")
                } else {
                    Log.e("MainActivity", "Usuário não encontrado")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao carregar usuário: ${e.message}")
            }
        } else {
            Log.w("MainActivity", "id_usuario não encontrado")
        }
        carregandoUsuario = false
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route

    val rotasComBarra = listOf(
        "profile", "home", "criar_grupo", "meus_grupos",
        "ebooks", "conversasPrivadas", "meu_calendario"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(onOptionSelected = { rota ->
                when (rota) {
                    "Chats" -> {
                        val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1
                        navController.navigate("conversasPrivadas/$idUsuario")
                    }

                    else -> navController.navigate(rota)
                }
                scope.launch { drawerState.close() }
            })
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                if (rotaAtual in rotasComBarra) {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            navController.navigate("home") {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    inclusive = false
                                                }
                                            }
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logoclaro),
                                        contentDescription = "Logo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Text(
                                    "Journey",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "tela_inicial",
                modifier = Modifier.padding(paddingValues)
            ) {
                // Autenticação
                composable("tela_inicial") { TelaInicial(navController) }
                composable("login") { Login(navController) }
                composable("cadastro") { Cadastro(navController) }
                composable("recuperacao_senha") { RecuperacaoSenha(navController) }

                // Perfil
                composable("profile") { Perfil(navController) }
                composable("editar_info/{idUsuario}") { backStackEntry ->
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull()
                    EditarInfoWrapper(navController, idUsuario)
                }

                // Home e grupos
                composable("home") { Home(navController) }
                composable("criar_grupo") { CriarGrupo(navegacao = navController) }
                composable("meus_grupos") { MeusGrupos(navController) }

                composable("grupoinfo/{id}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    GrupoInfo(navController, grupoId)
                }

                composable("home_grupo/{grupoId}/{idUsuario}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    HomeGrupo(navController, grupoId, idUsuario)
                }

                // Calendários
                composable("calendario/{grupoId}/{idUsuario}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    Calendario(navController, grupoId, idUsuario)
                }

                composable("meu_calendario") {
                    val context = LocalContext.current
                    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1
                    CalendarioPessoal(navController, idUsuario)
                }

                // Conversas privadas
                composable("conversasPrivadas/{idUsuario}") { backStack ->
                    val idUsuario = backStack.arguments?.getString("idUsuario")!!.toInt()
                    ConversasPrivadasScreen(navController, idUsuario)
                }

                composable("chatPrivado/{id}/{nome}/{idUsuario}") { backStack ->
                    val chatId = backStack.arguments?.getString("id")!!.toInt()
                    val nome = backStack.arguments?.getString("nome")!!
                    val idUsuario = backStack.arguments?.getString("idUsuario")!!.toInt()
                    ChatPrivadoScreen(navController, chatId, nome, idUsuario)
                }

                // Chat de grupo
                composable("chat_grupo/{idChatRoom}/{idUsuario}") { backStackEntry ->
                    val idChatRoom = backStackEntry.arguments?.getString("idChatRoom")?.toIntOrNull() ?: 0
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    ChatGrupoScreen(
                        navController = navController,
                        idChatRoom = idChatRoom,
                        idUsuario = idUsuario
                    )
                }

                // Ebooks
                composable("ebooks") {
                    TelaEbooksScreen(
                        onEbookClick = { id -> navController.navigate("ebook_detalhe/$id") },
                        onCriarClick = { navController.navigate("ebook_cadastrar") },
                        onCarrinhoClick = { navController.navigate("ebook_carrinho") }
                    )
                }

                composable("ebook_cadastrar") {
                    CadastrarEbookScreen(
                        onCancelar = { navController.popBackStack() },
                        onPublicar = {
                            navController.navigate("ebook_confirmar_publicacao")
                        }
                    )
                }

                composable("ebook_detalhe/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    DetalheEbookScreen(
                        onAdicionarCarrinho = { navController.navigate("ebook_carrinho") },
                        onVoltar = { navController.popBackStack() }
                    )
                }

                composable("ebook_carrinho") {
                    CarrinhoScreen(
                        onFinalizar = {
                            navController.navigate("ebooks") {
                                popUpTo("ebooks") { inclusive = true }
                            }
                        },
                        onVoltar = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
