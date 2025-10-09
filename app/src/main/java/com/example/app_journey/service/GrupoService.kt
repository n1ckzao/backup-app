package com.example.app_journey.service

import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.model.Result
import retrofit2.Call
import retrofit2.http.*

interface GrupoService {
    @Headers("Content-Type: application/json")
    @POST("group")
    fun inserirGrupo(@Body grupo: Grupo): Call<GruposResult>

    @GET("group")
    fun listarGrupos(): Call<GruposResult>


    @GET("group/{id}")
    fun getGrupoPorId(@Path("id")id : Int): Call<GruposResult>

    @GET("group")
    fun listarGruposDoUsuario(@Query("id_usuario") id_usuario: Int): Call<GruposResult>

}