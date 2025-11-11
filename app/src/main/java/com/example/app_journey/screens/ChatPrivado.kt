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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.SocketHandler
import io.socket.emitter.Emitter
import org.json.JSONObject

@Composable
fun ChatPrivadoScreen(
    navController: NavHostController,
    chatRoomId: Int,
    nomeOutroUsuario: String,
    idUsuarioAtual: Int
) {
    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var texto by remember { mutableStateOf("") }
    val listaState = rememberLazyListState()

    val socket = remember { SocketHandler.getSocket() }

    // Carrega mensagens iniciais e entra na sala
    LaunchedEffect(Unit) {
        mensagens = RetrofitInstance.mensagensService
            .listarMensagensPorSala(chatRoomId)
            .mensagens ?: emptyList()

        socket.emit("entrarSala", chatRoomId)
    }

    // Recebe mensagens em tempo real e remove listener ao sair da tela
    DisposableEffect(Unit) {
        val listener = Emitter.Listener { args ->
            val data = args[0] as JSONObject
            val novaMensagem = Mensagem(
                id_mensagens = data.getInt("id_mensagens"),
                conteudo = data.getString("conteudo"),
                enviado_em = data.optString("enviado_em"),
                id_chat = data.getInt("id_chat"),
                id_usuario = data.getInt("id_usuario")
            )

            mensagens = mensagens + novaMensagem
        }

        socket.on("novaMensagem", listener)

        onDispose {
            socket.off("novaMensagem", listener)
        }
    }

    // Scroll automático quando chegar nova mensagem
    LaunchedEffect(mensagens.size) {
        if (mensagens.isNotEmpty()) {
            listaState.animateScrollToItem(mensagens.size - 1)
        }
    }

    Column(Modifier.fillMaxSize()) {

        TopAppBar(title = { Text(nomeOutroUsuario) }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        })

        LazyColumn(state = listaState, modifier = Modifier.weight(1f)) {
            items(mensagens) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = if (msg.id_usuario == idUsuarioAtual)
                        Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .background(
                                if (msg.id_usuario == idUsuarioAtual) Color(0xFF6750A4)
                                else Color(0xFF4C36C3),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(text = msg.conteudo, color = Color.White)
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
                    val msg = texto
                    texto = ""

                    CoroutineScope(Dispatchers.IO).launch {
                        RetrofitInstance.mensagensService.enviarMensagem(
                            mapOf(
                                "id_chat" to chatRoomId,
                                "id_usuario" to idUsuarioAtual,
                                "conteudo" to msg
                            )
                        )
                    }

                    socket.emit("enviarMensagem", chatRoomId, idUsuarioAtual, msg)
                }
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "send")
            }
        }
    }
}
