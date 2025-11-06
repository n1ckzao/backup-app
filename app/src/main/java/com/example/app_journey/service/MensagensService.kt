package com.example.app_journey.service


import com.example.app_journey.model.Mensagem
import com.example.app_journey.model.MensagemResponse
import retrofit2.http.*

interface MensagensService {

    @GET("/v1/journey/chatroom/{id}/mensagens")
    suspend fun listarMensagensPorSala(@Path("id") chatRoomId: Int): MensagemResponse

    @POST("/v1/journey/mensagem")
    suspend fun enviarMensagem(@Body mensagem: Map<String, Any>): MensagemResponse
}