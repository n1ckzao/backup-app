package com.example.app_journey.model

data class ChatMessage(
    val id_usuario: Int,
    val id_chat_room: Int,
    val conteudo: String,
    val enviado_em: String? = null
)
