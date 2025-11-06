package com.example.app_journey.model

data class MensagemResponse(
    val status: Boolean,
    val status_code: Int,
    val itens: Int?,
    val mensagens: List<Mensagem>?
)

data class Mensagem(
    val id_mensagem: Int,
    val id_chat_room: Int,
    val id_usuario_remetente: Int,
    val texto: String,
    val data_envio: String?
)
