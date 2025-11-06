package com.example.app_journey.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.ChatRoomPrivado
import com.example.app_journey.service.RetrofitInstance

@Composable
fun ConversasPrivadasScreen(navController: NavHostController, idUsuario: Int) {
    var conversas by remember { mutableStateOf<List<ChatRoomPrivado>>(emptyList()) }

    LaunchedEffect(true) {
        conversas = RetrofitInstance.chatPrivadoService.listarConversasPrivadas(idUsuario)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(conversas) { sala ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("chatPrivado/${sala.id_chat_room}/${sala.nomeOutroUsuario}") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = sala.nomeOutroUsuario, fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
