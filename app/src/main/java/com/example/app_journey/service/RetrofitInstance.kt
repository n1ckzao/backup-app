package com.example.app_journey.service

import com.example.app_journey.network.ChatPrivadoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.107.140.11:3030/v1/journey/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarioService: UsuarioService = retrofit.create(UsuarioService::class.java)

    val grupoService: GrupoService = retrofit.create(GrupoService::class.java)

    val calendarioService: CalendarioService by lazy {
        retrofit.create(CalendarioService::class.java)
    }
    val mensagensService = retrofit.create(MensagensService::class.java)
    val chatPrivadoService = retrofit.create(ChatPrivadoService::class.java)


}