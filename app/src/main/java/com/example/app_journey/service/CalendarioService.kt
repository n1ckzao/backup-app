package com.example.app_journey.service

import com.example.app_journey.model.CalendarioItem
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import retrofit2.Call
import retrofit2.http.*

interface CalendarioService {

    // GET: todos os eventos
    @GET("v1/journey/calendario")
    fun getTodosEventos(): Call<CalendarioResponseWrapper>

    // GET: eventos por grupo
    @GET("v1/journey/grupo/{id}/calendario")
    fun getEventosPorGrupo(
        @Path("id") idGrupo: Int
    ): Call<CalendarioResponseWrapper>

    // GET: evento específico por id
    @GET("v1/journey/calendario/{id}")
    fun getEventoPorId(
        @Path("id") idEvento: Int
    ): Call<CalendarioResponseWrapper>

    // POST: criar novo evento
    @POST("v1/journey/calendario")
    fun criarEvento(
        @Body evento: NovoEventoRequest
    ): Call<CalendarioResponseWrapper>
}
