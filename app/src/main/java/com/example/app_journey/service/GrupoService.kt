package com.example.app_journey.service

import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GrupoResult
import com.example.app_journey.model.Result
import retrofit2.Call
import retrofit2.http.*

interface GrupoService {
    @Headers("Content-Type: application/json")
    @POST("group")
    fun inserirGrupo(@Body grupo: Grupo): Call<GrupoResult>

    @GET("group")
    fun listarGrupos(): Call<Result>

    @GET("group/{id}")
    fun getGrupoPorId(@Path("id")id : Int): Call<GrupoResult>
}

//interface GrupoService {
//    @Headers("Content-Type: application/json")
//    @POST("grupo")
//    fun inserirGrupo(@Body grupo: Grupo): Call<GrupoResult>
//
//    @GET("grupo")
//    fun listarGrupos(): Call<GrupoResult>
//
//    @GET("grupo/{id}")
//    fun getGrupoPorId(@Path("id") id: Int): Call<Grupo>
//}
