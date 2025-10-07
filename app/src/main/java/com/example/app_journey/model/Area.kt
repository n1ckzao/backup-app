package com.example.app_journey.model

data class Area(
    val id_area: Int,
    val area: String
)

data class AreaResult(
    val status: Boolean,
    val status_code: Int,
    val itens: Int,
    val areas: List<Area>
)
