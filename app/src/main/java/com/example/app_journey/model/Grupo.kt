package com.example.app_journey.model


data class GruposResult(
    val status: Boolean,
    val status_code: Int,
    val grupos: List<Grupo>
)

data class Grupo(
    val id_grupo: Int? = null,
    val nome: String,
    val limite_membros: Int,
    val descricao: String,
    val imagem: String?,
    val id_area: String,
    val id_usuario: Int
)


data class grupo_id(
    val id_grupo: Int
)

//data class Grupo(
//    val id_grupo: Int? = null,
//    val nome: String,
//    val limite_membros: Int,
//    val descricao: String,
//    val imagem: String,
//    val id_area: String,
//    val id_usuario: Int
//)

//data class GrupoResult(
//    val status: Boolean,
//    val status_code: Int,
//    val itens: Int,
//    val grupoID: List<grupo_id>,
//    val grupos: List<Grupo>
//)

