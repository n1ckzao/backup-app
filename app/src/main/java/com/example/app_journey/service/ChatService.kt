package com.example.app_journey.service

import com.example.app_journey.model.ChatMessage
import retrofit2.Call
import retrofit2.http.*

interface ChatService {

    // Buscar mensagens do banco (histórico)
    @GET("v1/journey/chatroom/{id}/mensagens")
    fun getMensagensPorSala(
        @Path("id") idChatRoom: Int
    ): Call<MensagensResponse>
}

data class MensagensResponse(
    val status_code: Int,
    val mensagens: List<ChatMessage>?
)
