package com.example.app_journey.service

import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EbookService {
    // GET: todos os eventos
    @GET("ebook")
    fun getTodosEventos(): Call<CalendarioResponseWrapper>

    // GET: evento específico por id
    @GET("ebook/:id")
    fun getEventoPorId(
        @Path("id") idEvento: Int
    ): Call<CalendarioResponseWrapper>

    // POST: criar novo evento
    @POST("ebook")
    fun criarEvento(
        @Body evento: NovoEventoRequest
    ): Call<CalendarioResponseWrapper>

    @DELETE("ebook/{id}")
    fun deleteEventoPorId(@Path("id") idEvento: Int): Call<Void>
}