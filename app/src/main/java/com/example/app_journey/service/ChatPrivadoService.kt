package com.example.app_journey.network

import com.example.app_journey.model.ChatRoomPrivado
import retrofit2.http.*

interface ChatPrivadoService {

    @POST("/v1/journey/chat-room/privado")
    suspend fun obterOuCriarSalaPrivada(@Body dados: Map<String, Int>): ChatRoomPrivado

    @GET("/v1/journey/usuario/{id_usuario}/conversas-privadas")
    suspend fun listarConversasPrivadas(@Path("id_usuario") idUsuario: Int): List<ChatRoomPrivado>
}
