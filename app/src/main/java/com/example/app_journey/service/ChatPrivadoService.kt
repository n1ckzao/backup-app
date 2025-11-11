package com.example.app_journey.network

import com.example.app_journey.model.Usuario
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

// Define o formato de resposta esperado do backend
data class ConversasPrivadasResponse(
    val status: Boolean? = null,
    val usuarios: List<Usuario>? = null,
    val mensagem: String? = null
)

// Interface Retrofit para Chat Privado
interface ChatPrivadoService {

    // Endpoint que lista todas as conversas privadas de um usuário
    @GET("usuario/{id_usuario}/conversas-privadas")
    suspend fun getConversasPrivadas(
        @Path("id_usuario") idUsuario: Int
    ): Response<ConversasPrivadasResponse>
}
