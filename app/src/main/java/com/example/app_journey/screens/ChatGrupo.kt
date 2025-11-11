package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Mensagem
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.utils.SocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatGrupoScreen(navController: NavHostController, idChatRoom: Int, idUsuario: Int) {
    val coroutineScope = rememberCoroutineScope()
    val socket = remember { SocketHandler.getSocket() }
    val listState = rememberLazyListState()

    var mensagens by remember { mutableStateOf<List<Mensagem>>(emptyList()) }
    var novaMensagem by remember { mutableStateOf("") }
    val usuarioCache = remember { mutableStateMapOf<Int, Pair<String, String?>>() }
    var socketInitialized by remember { mutableStateOf(false) }

    // 🔹 Carrega histórico do servidor
    LaunchedEffect(idChatRoom) {
        try {
            val response = RetrofitInstance.mensagemService.getMensagensPorSala(idChatRoom)
            if (response.isSuccessful) {
                val body = response.body()
                mensagens = body?.mensagens ?: emptyList()
                Log.d("ChatGrupo", "✅ Histórico carregado (${mensagens.size} mensagens)")
            } else {
                Log.e("ChatGrupo", "Erro ao carregar histórico: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("ChatGrupo", "Erro: ${e.localizedMessage}")
        }
    }

    // 🔹 Inicializa o socket e escuta novas mensagens
    LaunchedEffect(Unit) {
        if (!socketInitialized) {
            SocketHandler.init()
            SocketHandler.connect()
            SocketHandler.joinRoom(idChatRoom)
            socketInitialized = true
        }

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

                coroutineScope.launch(Dispatchers.Main) {
                    mensagens = mensagens + novaMsg
                    listState.animateScrollToItem(mensagens.size - 1)
                }
            }
        }
    }

    // 🔹 Layout principal
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat do Grupo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF341E9B)),
                navigationIcon = {
                    IconButton(onClick = {
                        SocketHandler.leaveRoom(idChatRoom)
                        navController.popBackStack()
                    }) {
                        Icon(
                            painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/2223/2223615.png"),
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFEDEEFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // 🔹 Lista de mensagens
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(mensagens.size) { index ->
                    val msg = mensagens[index]

                    // Busca nome/foto do usuário, se ainda não tiver
                    val usuarioInfo = usuarioCache[msg.id_usuario]
                    if (usuarioInfo == null) {
                        LaunchedEffect(msg.id_usuario) {
                            try {
                                val usuarioResp =
                                    RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(msg.id_usuario)
                                val usuario = usuarioResp.usuario?.firstOrNull()
                                usuarioCache[msg.id_usuario] =
                                    (usuario?.nome_completo ?: "Desconhecido") to usuario?.foto_perfil
                            } catch (e: Exception) {
                                Log.e("ChatGrupo", "Erro ao carregar usuário: ${e.message}")
                            }
                        }
                    }

                    val (nome, foto) = usuarioCache[msg.id_usuario]
                        ?: ("Carregando..." to null)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = if (msg.id_usuario == idUsuario)
                            Arrangement.End else Arrangement.Start
                    ) {
                        if (msg.id_usuario != idUsuario) {
                            Image(
                                painter = rememberAsyncImagePainter(foto),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Column(
                            modifier = Modifier
                                .background(
                                    if (msg.id_usuario == idUsuario) Color(0xFFB5A9FF)
                                    else Color.White,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                                .widthIn(max = 260.dp)
                        ) {
                            if (msg.id_usuario != idUsuario) {
                                Text(
                                    text = nome,
                                    fontSize = 13.sp,
                                    color = Color(0xFF341E9B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = msg.conteudo,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // 🔹 Caixa de envio
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = novaMensagem,
                    onValueChange = { novaMensagem = it },
                    placeholder = { Text("Digite uma mensagem...") },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (novaMensagem.isNotBlank()) {
                            val json = JSONObject().apply {
                                put("conteudo", novaMensagem)
                                put("id_chat_room", idChatRoom)
                                put("id_usuario", idUsuario)
                            }

                            SocketHandler.sendMessage(json)
                            novaMensagem = ""
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
