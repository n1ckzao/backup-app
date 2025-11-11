package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilPublicoScreen(
    navController: NavController,
    userId: Int,
    currentUserId: Int
) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        carregarUsuario(userId, coroutineScope) { result ->
            usuario = result.getOrNull()
            if (result.isFailure) {
                erro = result.exceptionOrNull()?.message ?: "Erro ao carregar perfil"
            }
            carregando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (carregando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (erro != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(erro ?: "Erro desconhecido", color = MaterialTheme.colorScheme.error)
            }
        } else if (usuario == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Usuário não encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil
                Image(
                    painter = if (!usuario?.foto_perfil.isNullOrEmpty()) {
                        rememberAsyncImagePainter(usuario?.foto_perfil)
                    } else {
                        painterResource(id = R.drawable.logoclaro)
                    },
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nome do usuário
                Text(
                    text = usuario?.nome_completo ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bio/Descrição
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = usuario?.descricao ?: "Sem descrição",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botão para iniciar conversa
                Button(
                    onClick = {
                        navController.navigate("chatPrivado/$userId/${usuario?.nome_completo}/$currentUserId")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text("Chamar para conversar", fontSize = 16.sp)
                }
            }
        }
    }
}

private fun carregarUsuario(
    userId: Int,
    coroutineScope: CoroutineScope,
    onResult: (Result<Usuario>) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = withContext(Dispatchers.IO) {
                try {
                    RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(userId)
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null && response.usuario?.isNotEmpty() == true) {
                onResult(Result.success(response.usuario.first()))
            } else {
                onResult(Result.failure(Exception("Usuário não encontrado")))
            }
        } catch (e: Exception) {
            onResult(Result.failure(e))
        }
    }
}
