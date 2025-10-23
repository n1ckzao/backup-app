package com.example.app_journey.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun HomeGrupo(
    navController: NavHostController,
    grupoId: Int
) {
    var grupo by remember { mutableStateOf<com.example.app_journey.model.Grupo?>(null) }
    var carregandoDados by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(grupoId) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.grupoService.getGrupoById(grupoId).execute()
            }
            if (response.isSuccessful) {
                val wrapper = response.body()
                if (wrapper != null && wrapper.grupo.isNotEmpty()) {
                    grupo = wrapper.grupo[0]
                } else {
                    erroMsg = "Grupo não encontrado"
                }
            } else {
                erroMsg = "Erro: ${response.code()}"
            }
        } catch (e: Exception) {
            erroMsg = "Erro: ${e.localizedMessage}"
        } finally {
            carregandoDados = false
        }
    }


    val nome = grupo?.nome ?: "Grupo"
    val descricao = grupo?.descricao ?: "Sem descrição"
    val imagem = grupo?.imagem ?: ""
    val membros = grupo?.limite_membros ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEEFF))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF341E9B
                    ))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (carregandoDados) {
            CircularProgressIndicator()
        } else if (erroMsg != null) {
            Text(text = erroMsg ?: "Erro", color = Color.Red)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1E3FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = if (imagem.isNotEmpty()) rememberAsyncImagePainter(imagem)
                            else painterResource(id = R.drawable.logoclaro),
                            contentDescription = nome,
                            modifier = Modifier.size(80.dp).padding(end = 12.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column {
                            Text(text = nome, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Text(text = "$membros membros", color = Color.Gray, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Descrição:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF341E9B))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFD6D3F9), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = descricao, color = Color(0xFF1E1E1E))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate("chat_grupo/${grupoId}") },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF341E9B))
                        ) {
                            Text("Chat", color = Color.White, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                navController.navigate("calendario/${grupoId}")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF341E9B))
                        ) {
                            Text("Calendário", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
