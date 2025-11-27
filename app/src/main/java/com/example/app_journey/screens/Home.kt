package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.R
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

    // Carregar áreas
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
        grupos.filter { grupo -> grupo.id_area == (areaSelecionadaObj?.id_area ?: -1) }
    }

    // Layout da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5FF))
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Bem-vindo ao Journey!",
            fontSize = 28.sp,
            color = Color(0xFF341E9B),
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Plataforma para mentoria e aprendizado colaborativo",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card principal
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF341E9B)),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Grupos",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fila de botões
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botão "+ Criar Grupo" com glow animado
                    val infiniteTransition = rememberInfiniteTransition()
                    val glowAnim by infiniteTransition.animateFloat(
                        initialValue = 8f,
                        targetValue = 20f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Button(
                        onClick = { navegacao.navigate("criar_grupo") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.shadow(glowAnim.dp, shape = RoundedCornerShape(24.dp))
                    ) {
                        Text("+ Criar Grupo", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }

                    // Dropdown categorias
                    Box {
                        Button(
                            onClick = { expanded = !expanded },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("✔ $categoriaSelecionada", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text("Todas") }, onClick = {
                                categoriaSelecionada = "Todas"
                                expanded = false
                            })
                            areas.forEach { area ->
                                DropdownMenuItem(text = { Text(area.area) }, onClick = {
                                    categoriaSelecionada = area.area
                                    expanded = false
                                })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Lista de grupos
                // Lista de grupos
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(gruposFiltrados) { grupo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navegacao.navigate("grupoinfo/${grupo.id_grupo}") },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFDAD5FF)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Imagem do grupo
                                val painter = if (!grupo.imagem.isNullOrBlank()) {
                                    rememberAsyncImagePainter(grupo.imagem)
                                } else {
                                    painterResource(R.drawable.logoclaro) // placeholder local
                                }
                                Image(
                                    painter = painter,
                                    contentDescription = grupo.nome,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Texto do grupo
                                Column {
                                    Text(
                                        grupo.nome,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF341E9B)
                                    )
                                    Text(
                                        "${grupo.limite_membros} membros",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        "Área: ${areas.find { it.id_area == grupo.id_area }?.area ?: "Desconhecida"}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}