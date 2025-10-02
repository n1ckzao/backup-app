package com.example.app_journey.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.107.144.11:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarioService: UsuarioService = retrofit.create(UsuarioService::class.java)
}