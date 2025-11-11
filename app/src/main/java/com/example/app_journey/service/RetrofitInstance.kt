package com.example.app_journey.service

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor { message -> 
        Log.d("HTTP", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.107.144.12:3030/v1/journey/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarioService: UsuarioService = retrofit.create(UsuarioService::class.java)

    val grupoService: GrupoService = retrofit.create(GrupoService::class.java)

    val calendarioService: CalendarioService by lazy {
        retrofit.create(CalendarioService::class.java)
    }
    val mensagemService: MensagemService by lazy {
        retrofit.create(MensagemService::class.java)
    }

    val chatPrivadoService: ChatPrivadoService by lazy {
        retrofit.create(ChatPrivadoService::class.java)
    }

    val areaService: AreaService by lazy {
        retrofit.create(AreaService::class.java)
    }

}