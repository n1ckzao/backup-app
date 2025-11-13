// MainActivity.kt
package com.example.app_journey

import android.content.Context
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
import com.example.app_journey.ui.theme.JourneyTheme
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch

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
    var isDarkTheme by remember { mutableStateOf(false) }
    var usuarioLogado by remember { mutableStateOf<Usuario?>(null) }
    var carregandoUsuario by remember { mutableStateOf(true) }

    val idUsuarioLogado = usuarioLogado?.id_usuario ?: -1
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Carregar usuário salvo
    LaunchedEffect(Unit) {
        val idSalvo = SharedPrefHelper.recuperarIdUsuario(context)
        if (idSalvo != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idSalvo)
                if (!result.usuario.isNullOrEmpty()) usuarioLogado = result.usuario[0]
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro de rede: ${e.message}")
            }
        }
        carregandoUsuario = false
    }

    // Observa rota atual
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry.value?.destination?.route
    val rotasComBarra = listOf(
        "profile", "home/{idUsuario}", "criar_grupo",
        "editar_info/{idUsuario}", "meus_grupos", "ebooks"
    )

    JourneyTheme(darkTheme = isDarkTheme) {

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerMenu(
                    idUsuario = idUsuarioLogado,
                    onOptionSelected = { rota ->
                        navController.navigate(rota)
                        scope.launch { drawerState.close() }
                    }
                )
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


                                    // --- LOGO COM BASE NO TEMA ---
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                val idUsuario = SharedPrefHelper.recuperarIdUsuario(context)
                                                if (idUsuario != null) {
                                                    navController.navigate("home/$idUsuario") {
                                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                                    }
                                                }
                                            }
                                    ) {
                                        val logoId = if (isDarkTheme) R.drawable.logo else R.drawable.logoclaro
                                        Image(
                                            painter = painterResource(id = logoId),
                                            contentDescription = "Logo",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    // --- TÍTULO ---
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
                                // --- TOGGLE DE TEMA ---
                                IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                                    val icon = if (isDarkTheme) R.drawable.sun else R.drawable.moon
                                    Icon(
                                        painter = painterResource(id = icon),
                                        contentDescription = "Alternar Tema"
                                    )
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
                    // Aqui você mantém todas as suas rotas existentes
                    composable("login") { Login(navController) }
                    composable("cadastro") { Cadastro(navController) }
                    composable("recuperacao_senha") { RecuperacaoSenha(navController) }
                    composable("tela_inicial") { TelaInicial(navController) }
                    composable("home/{idUsuario}") { backStack ->
                        val idUsuario = backStack.arguments?.getString("idUsuario")!!.toInt()
                        Home(navController, idUsuario)
                    }
                    composable("profile") { Perfil(navController) }
                    composable("criar_grupo") { CriarGrupo(navegacao = navController) }
                    composable("meus_grupos") { MeusGrupos(navController) }
                    composable("editar_info/{idUsuario}") { backStackEntry ->
                        val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull()
                        EditarInfoWrapper(navController, idUsuario)
                    }
                    // e todas as outras rotas que você tinha...
                }
            }
        }
    }
}
