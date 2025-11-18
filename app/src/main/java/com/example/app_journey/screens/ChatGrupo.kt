package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

data class MensagemUI(
    val id: Int,
    val texto: String,
    val remetente: String,
    val idRemetente: Int,
    val isUser: Boolean
)



@Composable
fun ChatGrupo(
    navController: NavHostController,
    grupoId: Int,
    idUsuarioAtual: Int
)
{
    var mensagens by remember {
        mutableStateOf(
            listOf(
                MensagemUI(1, "Olá pessoal!", "Maria", idRemetente = 2, isUser = false),
                MensagemUI(2, "Oi Maria, tudo bem?", "Você", idRemetente = 1, isUser = true),
                MensagemUI(3, "Vamos revisar o material hoje?", "Lucas", idRemetente = 3, isUser = false)
            )
        )

    }

    var novaMensagem by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6D3F9))
            .padding(horizontal = 8.dp)
    ) {
        // 🔹 Barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF341E9B))
            }
            Text(
                text = "Chat do Grupo",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF341E9B),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Divider(color = Color(0xFFB8AFFF), thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Lista de mensagens
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mensagens) { msg ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("chatPrivado/${grupoId}/${msg.idRemetente}/$idUsuarioAtual")
                        },
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {

                    if (!msg.isUser) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF4C36C3), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(
                                if (msg.isUser) Color(0xFF6750A4) else Color(0xFF4C36C3),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.texto,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }


                    if (msg.isUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF4C36C3), CircleShape)
                        )
                    }
                }
            }
        }

        // 🔹 Campo de envio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = novaMensagem,
                onValueChange = { novaMensagem = it },
                placeholder = { Text("Digite uma mensagem...") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF4C36C3)
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                )
            )


            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (novaMensagem.isNotBlank()) {
                        scope.launch {
                            mensagens = mensagens + MensagemUI(
                                id = mensagens.size + 1,
                                texto = novaMensagem.trim(),
                                remetente = "Você",
                                idRemetente = idUsuarioAtual,
                                isUser = true
                            )
                            novaMensagem = ""
                        }
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF4C36C3), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}