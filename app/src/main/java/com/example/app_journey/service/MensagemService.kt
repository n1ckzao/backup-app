package com.example.app_journey.service

import com.example.app_journey.model.Mensagem
import retrofit2.Response
import retrofit2.http.*

data class MensagemResponse(
    val status: Boolean? = null,
    val status_code: Int? = null,
    val itens: Int? = null,
    val mensagem: Mensagem? = null,
    val mensagens: List<Mensagem>? = null
)

interface MensagemService {

    // !!!! deixar sem o v1/journey....
    @GET("chatroom/{id}/mensagens")
    suspend fun getMensagensPorSala(@Path("id") idChatRoom: Int): Response<MensagemResponse>

    // envia mensagem
    @POST("v1/journey/mensagem")
    suspend fun enviarMensagem(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<MensagemResponse>
}
