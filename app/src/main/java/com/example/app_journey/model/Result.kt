package com.example.app_journey.model

data class Result(
    val status: Boolean,
    val status_code: Int,
    val itens: Int,
    val usuario: List<Usuario>
)