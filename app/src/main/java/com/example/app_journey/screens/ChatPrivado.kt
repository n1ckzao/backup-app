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
import com.example.app_journey.model.MensagemResponse
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import io.socket.client.Socket

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
            Log.d("ChatPrivado", "Buscando mensagens para a sala: $idChatRoom")
            
            // Primeiro tenta obter as mensagens da sala privada
            val response = try {
                withContext(Dispatchers.IO) {
                    RetrofitInstance.chatPrivadoService.getMensagensPrivadas(idChatRoom)
                }
            } catch (e: Exception) {
                Log.e("ChatPrivado", "Erro ao buscar mensagens: ${e.message}", e)
                null
            }
            
            if (response?.isSuccessful == true) {
                val responseBody = response.body()
                Log.d("ChatPrivado", "Resposta da API: $responseBody")
                
                val mensagensRecebidas = responseBody?.mensagens ?: emptyList()
                Log.d("ChatPrivado", "${mensagensRecebidas.size} mensagens carregadas")
                
                // Ordena as mensagens por data de envio
                mensagens = mensagensRecebidas.sortedBy { it.enviado_em ?: "" }
                
                // Log para depuração
                mensagens.forEachIndexed { index, msg ->
                    Log.d("ChatPrivado", "Mensagem ${index + 1}: ${msg.conteudo} (${msg.enviado_em})")
                }
            } else {
                val errorBody = response?.errorBody()?.string()
                Log.e("ChatPrivado", "Erro ao carregar histórico. Código: ${response?.code()}, Resposta: $errorBody")
            }
        } catch (e: Exception) {
            Log.e("ChatPrivado", "Erro ao carregar mensagens", e)
        }
    }

    // Socket real-time
    LaunchedEffect(Unit) {
        try {
            Log.d("ChatPrivado", "Iniciando configuração do socket...")
            
            // Inicializa e conecta o socket
            SocketHandler.init()
            SocketHandler.connect()
            
            // Entra na sala do chat
            SocketHandler.joinRoom(idChatRoom)
            
            val socket = SocketHandler.getSocket()
            if (socket == null) {
                Log.e("ChatPrivado", "❌ Falha ao obter instância do socket")
                return@LaunchedEffect
            }
            
            Log.d("ChatPrivado", "✅ Socket configurado com sucesso")
            
            // Configura o listener para novas mensagens
            socket.on("receive_message") { args ->
                try {
                    if (args.isNotEmpty() && args[0] is JSONObject) {
                        val data = args[0] as JSONObject
                        Log.d("ChatPrivado", "Nova mensagem recebida: $data")
                        
                        val novaMsg = Mensagem(
                            id_mensagens = data.optInt("id_mensagens"),
                            conteudo = data.optString("conteudo"),
                            id_chat_room = data.optInt("id_chat_room"),
                            id_usuario = data.optInt("id_usuario"),
                            enviado_em = data.optString("enviado_em")
                        )
                        
                        // Atualiza a lista de mensagens na thread principal
                        coroutineScope.launch {
                            mensagens = mensagens + novaMsg
                        }
                        
                        Log.d("ChatPrivado", "Mensagem adicionada: ${novaMsg.conteudo}")
                    } else {
                        Log.e("ChatPrivado", "Formato de mensagem inválido: ${args.contentToString()}")
                    }
                } catch (e: Exception) {
                    Log.e("ChatPrivado", "Erro ao processar mensagem do socket", e)
                }
            }
            
            // Configura listeners de erro e conexão
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("ChatPrivado", "✅ Conectado ao servidor de sockets")
            }
            
            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d("ChatPrivado", "❌ Desconectado do servidor de sockets")
            }
            
            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("ChatPrivado", "❌ Erro de conexão: ${args.contentToString()}")
            }
            
        } catch (e: Exception) {
            Log.e("ChatPrivado", "Erro na configuração do socket", e)
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
