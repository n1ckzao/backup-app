package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.Area
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.model.AreaResult
import com.example.app_journey.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navegacao: NavHostController, idUsuario: Int) {
    val context = LocalContext.current

    val grupos = remember { mutableStateListOf<Grupo>() }
    val areas = remember { mutableStateListOf<Area>() }

    var categoriaSelecionada by remember { mutableStateOf("Todas") }
    var expanded by remember { mutableStateOf(false) }

    // Carregar grupos
    LaunchedEffect(Unit) {
        RetrofitFactory().getGrupoService().listarGrupos()
            .enqueue(object : Callback<GruposResult> {
                override fun onResponse(call: Call<GruposResult>, response: Response<GruposResult>) {
                    if (response.isSuccessful) {
                        response.body()?.grupos?.let {
                            grupos.clear()
                            grupos.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<GruposResult>, t: Throwable) {
                    Toast.makeText(context, "Erro ao carregar grupos", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Carregar áreas (categorias)
    LaunchedEffect(Unit) {
        RetrofitFactory().getAreaService().listarAreas()
            .enqueue(object : Callback<AreaResult> {
                override fun onResponse(call: Call<AreaResult>, response: Response<AreaResult>) {
                    if (response.isSuccessful) {
                        response.body()?.areas?.let {
                            areas.clear()
                            areas.addAll(it)
                        }
                    }
                }

                override fun onFailure(call: Call<AreaResult>, t: Throwable) {
                    Toast.makeText(context, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
                }
            })
    }

    val areaSelecionadaObj = areas.find { it.area == categoriaSelecionada }

    val gruposFiltrados = if (categoriaSelecionada == "Todas") {
        grupos
    } else {
        grupos.filter { grupo ->
            grupo.id_area == (areaSelecionadaObj?.id_area ?: -1)
        }
    }

    // Layout da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FF))
            .padding(16.dp)
    ) {
        Text(
            text = "Bem-vindo ao Journey!",
            fontSize = 26.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Uma plataforma para mentoria e\naprendizado colaborativo",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF341E9B)),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Grupos",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navegacao.navigate("criar_grupo") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("+ Criar Grupo", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }

                    // Dropdown de categorias
                    Box {
                        Button(
                            onClick = { expanded = !expanded },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("✔ $categoriaSelecionada", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todas") },
                                onClick = {
                                    categoriaSelecionada = "Todas"
                                    expanded = false
                                }
                            )
                            areas.forEach { area ->
                                DropdownMenuItem(
                                    text = { Text(area.area) },
                                    onClick = {
                                        categoriaSelecionada = area.area
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(gruposFiltrados) { grupo ->
                        GrupoCard(grupo = grupo) {
                            navegacao.navigate("grupoinfo/${grupo.id_grupo}")
                        }
                    }
                }
            }
        }
    }
}
