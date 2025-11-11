package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPrivadoScreen(
    navController: NavHostController,
    idChatRoom: Int,
    nome: String,
    idUsuario: Int
) {
    val coroutineScope = rememberCoroutineScope()
    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var novaMensagem by remember { mutableStateOf(TextFieldValue("")) }

    // Histórico inicial
    LaunchedEffect(idChatRoom) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.mensagemService.getMensagensPorSala(idChatRoom)
            }
            if (response.isSuccessful) {
                mensagens = response.body()?.mensagens ?: emptyList()
            } else {
                Log.e("ChatPrivado", "Erro ao carregar histórico: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ChatPrivado", "Erro: ${e.message}")
        }
    }

    // Socket real-time
    LaunchedEffect(Unit) {
        SocketHandler.init()
        SocketHandler.connect()
        SocketHandler.joinRoom(idChatRoom)

        val socket = SocketHandler.getSocket()
        socket?.on("receive_message") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val novaMsg = Mensagem(
                    id_mensagens = data.optInt("id_mensagens"),
                    conteudo = data.optString("conteudo"),
                    id_chat_room = data.optInt("id_chat_room"),
                    id_usuario = data.optInt("id_usuario"),
                    enviado_em = data.optString("enviado_em")
                )
                mensagens = mensagens + novaMsg
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = nome, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        SocketHandler.leaveRoom(idChatRoom)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF341E9B))
            )
        },
        containerColor = Color(0xFFEDEEFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensagens) { msg ->
                    val isMine = msg.id_usuario == idUsuario
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isMine) Color(0xFF341E9B) else Color.White,
                            modifier = Modifier.widthIn(max = 260.dp)
                        ) {
                            Text(
                                msg.conteudo,
                                color = if (isMine) Color.White else Color.Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = novaMensagem,
                    onValueChange = { novaMensagem = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp),
                    placeholder = { Text("Digite uma mensagem...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val texto = novaMensagem.text.trim()
                        if (texto.isNotEmpty()) {
                            val json = JSONObject().apply {
                                put("conteudo", texto)
                                put("id_chat_room", idChatRoom)
                                put("id_usuario", idUsuario)
                            }
                            SocketHandler.sendMessage(json)
                            novaMensagem = TextFieldValue("")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF341E9B))
                ) {
                    Text("Enviar", color = Color.White)
                }
            }
        }
    }
}
