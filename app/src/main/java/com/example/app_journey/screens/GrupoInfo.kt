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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
import com.example.app_journey.model.Grupo

@Composable
fun GrupoInfo(
    navController: NavHostController,
    grupo: Grupo? = null // Pode receber o grupo selecionado
) {
    val nome = grupo?.nome ?: "Direito"
    val descricao = grupo?.descricao ?: "Discussões sobre temas jurídicos e estudo de casos."
    val imagem = grupo?.imagem ?: ""
    val area = "Direito"
    val membros = grupo?.limite_membros ?: 15

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
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
                        else painterResource(id = R.drawable.logoclaro), // coloque um placeholder no drawable
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

                // 🏷️ Área
                Text(
                    text = "Área:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF341E9B)
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFD6D3F9),
                        disabledTextColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        onClick = { /* Navegar para chat */ },
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
                        onClick = { /* Navegar para calendário */ },
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

                // 🟣 Botão Participar
                Button(
                    onClick = { /* Implementar participação */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text(
                        "Participar",
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
