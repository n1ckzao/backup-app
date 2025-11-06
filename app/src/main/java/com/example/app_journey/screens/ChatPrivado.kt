package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChatPrivadoScreen(
    navController: NavHostController,
    chatRoomId: Int,
    nomeOutroUsuario: String,
    idUsuarioAtual: Int
) {
    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var texto by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        mensagens =
            (RetrofitInstance.mensagensService.listarMensagensPorSala(chatRoomId).mensagens ?: emptyList()) as List<Mensagem>
    }

    Column(Modifier.fillMaxSize()) {

        TopAppBar(title = { Text(nomeOutroUsuario) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        })

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mensagens) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.id_usuario_remetente == idUsuarioAtual)
                        Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .background(
                                if (msg.id_usuario_remetente == idUsuarioAtual) Color(0xFF6750A4)
                                else Color(0xFF4C36C3),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.texto,
                            color = Color.White,
                            fontSize = 15.sp,
                            textAlign = if (msg.id_usuario_remetente == idUsuarioAtual)
                                TextAlign.End else TextAlign.Start
                        )
                    }
                }
            }

        }

        Row(Modifier.fillMaxWidth().padding(8.dp)) {
            TextField(
                value = texto,
                onValueChange = { texto = it },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (texto.isNotBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        RetrofitInstance.mensagensService.enviarMensagem(
                            mapOf(
                                "id_chat_room" to chatRoomId,
                                "id_usuario_remetente" to idUsuarioAtual,
                                "texto" to texto
                            )
                        )
                        mensagens = (RetrofitInstance.mensagensService.listarMensagensPorSala(chatRoomId).mensagens ?: emptyList()) as List<Mensagem>
                        texto = ""
                    }
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "enviar")
            }
        }
    }
}
