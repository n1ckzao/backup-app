// MainActivity.kt
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResult
import com.example.app_journey.screens.*
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.ui.theme.PurpleMedium
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import retrofit2.*

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
    LaunchedEffect(Unit) {
        val idSalvo = SharedPrefHelper.recuperarIdUsuario(context)
        Log.d("MainActivity", "ID salvo: $idSalvo")

        if (idSalvo != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idSalvo)
                Log.d("MainActivity", "Resposta API: $result")
                if (!result.usuario.isNullOrEmpty()) {
                    usuarioLogado = result.usuario[0]
                    Log.d("MainActivity", "Usuário carregado: ${usuarioLogado?.nome_completo}")
                } else {
                    Log.e("MainActivity", "Usuário não encontrado")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro de rede: ${e.message}")
            }
        } else {
            Log.w("MainActivity", "id_usuario não encontrado")
        }
        carregandoUsuario = false
    }


    // Observa a rota atual
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route

    // Rotas que exibem AppBar + Drawer
    val rotasComBarra = listOf("profile", "home", "criar_grupo", "editar_info/{idUsuario}", "meus_grupos", "ebooks")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(onOptionSelected = { rota ->
                navController.navigate(rota)
                scope.launch { drawerState.close() }
            })
        },
        gesturesEnabled = drawerState.isOpen
    )
    {
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
                startDestination = "login",
                modifier = Modifier.padding(paddingValues)
            ) {
                // Rotas existentes
                composable("login") { Login(navController) }
                composable("cadastro") { Cadastro(navController) }
                composable("recuperacao_senha") { RecuperacaoSenha(navController) }
                composable("home") { Home(navController) }
                composable("profile") { Perfil(navController) }
                composable("criar_grupo") { CriarGrupo(navegacao = navController) }
                composable("meus_grupos") { MeusGrupos(navController) }

                composable("editar_info/{idUsuario}") { backStackEntry ->
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull()
                    EditarInfoWrapper(navController, idUsuario)
                }//edição do perfil

                composable(
                    route = "grupoinfo/{id}",
                ) { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    GrupoInfo(navController = navController, grupoId = grupoId)
                }
                composable("home_grupo/{grupoId}/{idUsuario}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    HomeGrupo(navController = navController, grupoId = grupoId, idUsuario = idUsuario)
                }

                composable("calendario/{grupoId}/{idUsuario}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                    Calendario(navController = navController, grupoId = grupoId, idUsuario = idUsuario)
                }
                composable("chat_grupo/{grupoId}") { backStackEntry ->
                    val grupoId = backStackEntry.arguments?.getString("grupoId")?.toIntOrNull() ?: 0
                    ChatGrupo(navController = navController, grupoId = grupoId)
                }

                composable("verificar_email/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    email?.let { VerificarEmail(navController, it) }
                }

                composable("redefinir_senha/{idUsuario}") { backStackEntry ->
                    val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull()
                    idUsuario?.let { RedefinirSenha(navController, it) }
                }

                //Ebooks

                // Tela inicial: lista de e-books
                composable("ebooks") {
                    TelaEbooksScreen(
                        onEbookClick = { id ->
                            navController.navigate("ebook_detalhe/$id")
                        },
                        onCriarClick = {
                            navController.navigate("ebook_cadastrar")
                        },
                        onCarrinhoClick = {
                            navController.navigate("ebook_carrinho")
                        }
                    )
                }

                // Cadastro de e-book
                composable("ebook_cadastrar") {
                    CadastrarEbookScreen(
                        onCancelar = { navController.popBackStack() },
                        onPublicar = {
                            navController.navigate("ebook_confirmar_publicacao")
                        }
                    )
                }

                // Detalhe do e-book
                composable("ebook_detalhe/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                    DetalheEbookScreen(
                        onAdicionarCarrinho = {
                            navController.navigate("ebook_carrinho")
                        },
                        onVoltar = { navController.popBackStack() }
                    )
                }

                // Tela do carrinho
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
