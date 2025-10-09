package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Grupo
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeusGrupos(navController: NavHostController) {
    val context = LocalContext.current
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var grupos by remember { mutableStateOf<List<Grupo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                loading = true
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.grupoService.listarGruposDoUsuario(idUsuario).execute()
                }

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.status) {
                        grupos = result.grupos.filter { grupo ->
                            grupo.id_usuario == idUsuario
                        }
                    } else {
                        errorMessage = "Nenhum grupo encontrado."
                    }
                } else {
                    errorMessage = "Erro HTTP: ${response.code()}"
                }

            } catch (e: Exception) {
                errorMessage = "Erro: ${e.localizedMessage}"
            } finally {
                loading = false
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logoclaro),
                            contentDescription = "Logo Journey",
                            modifier = Modifier.fillMaxHeight()
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Journey", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF351D9B))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFD9DCFC))
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(grupos) { grupo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF351D9B))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!grupo.imagem.isNullOrBlank()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(grupo.imagem),
                                            contentDescription = grupo.nome,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.logo),
                                            contentDescription = grupo.nome,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            grupo.nome,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${grupo.limite_membros} membros",
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
