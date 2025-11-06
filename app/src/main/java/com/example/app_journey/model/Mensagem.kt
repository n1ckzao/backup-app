package com.example.app_journey.model

data class MensagemResponse(
    val status: Boolean,
    val status_code: Int,
    val itens: Int?,
    val mensagens: List<Mensagem>?
)

data class Mensagem(
    val id_mensagens: Int,
    val conteudo: String,
    val enviado_em: String?,
    val id_chat: Int,
    val id_usuario: Int
)

