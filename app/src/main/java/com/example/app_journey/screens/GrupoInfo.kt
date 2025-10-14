package com.example.app_journey.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Grupo
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GrupoInfo(
    navController: NavHostController,
    grupo: Grupo? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var participando by remember { mutableStateOf(false) }
    var carregando by remember { mutableStateOf(false) }

    val nome = grupo?.nome ?: "Grupo sem nome"
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
        // 🔙 Ícone de voltar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logoclaro),
                contentDescription = "Voltar",
                tint = Color(0xFF341E9B),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📦 Card principal
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1E3FF)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 📷 Imagem + Nome
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = if (imagem.isNotEmpty()) rememberAsyncImagePainter(imagem)
                        else painterResource(id = R.drawable.logoclaro),
                        contentDescription = nome,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 12.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column {
                        Text(
                            text = nome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF1E1E1E)
                        )
                        Text(
                            text = "$membros membros",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📝 Descrição
                Text(
                    text = "Descrição:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF341E9B)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFD6D3F9), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = descricao,
                        color = Color(0xFF1E1E1E),
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔘 Botões Chat e Calendário
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* Chat */ },
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
                        onClick = { /* Calendário */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF341E9B))
                    ) {
                        Text("Calendário", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🔘 Botão Participar
                Button(
                    onClick = {
                        if (grupo?.id_grupo == null) {
                            Toast.makeText(context, "Grupo inválido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            carregando = true
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    RetrofitInstance.grupoService
                                        .participarDoGrupo(
                                            grupo.id_grupo,
                                            mapOf("id_usuario" to idUsuario)
                                        )
                                        .execute()
                                }

                                if (response.isSuccessful && response.body()?.status == true) {
                                    participando = true
                                    Toast.makeText(context, "Você agora participa do grupo!", Toast.LENGTH_SHORT).show()
                                    // ✅ Volta para a tela MeusGrupos e ela pode recarregar
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Erro ao entrar no grupo", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            } finally {
                                carregando = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !participando && !carregando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (participando) Color(0xFF4CAF50) else Color(0xFF6750A4)
                    )
                ) {
                    Text(
                        when {
                            carregando -> "Entrando..."
                            participando -> "Participando"
                            else -> "Participar"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewGrupoInfo() {
    val fakeNav = androidx.navigation.compose.rememberNavController()
    GrupoInfo(navController = fakeNav)
}
