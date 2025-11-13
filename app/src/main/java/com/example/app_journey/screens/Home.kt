package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.app_journey.model.Area
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.model.AreaResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.TutorialPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navegacao: NavHostController, idUsuario: Int) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val grupos = remember { mutableStateListOf<Grupo>() }
    val areas = remember { mutableStateListOf<Area>() }

    var categoriaSelecionada by remember { mutableStateOf("Todas") }
    var expanded by remember { mutableStateOf(false) }

    // === Controle do tutorial ===
    var mostrarTutorial by remember { mutableStateOf(false) }
    var tutorialStep by remember { mutableStateOf(0) }
    var criarGrupoPos by remember { mutableStateOf(Offset.Zero) }
    var cardPos by remember { mutableStateOf(Offset.Zero) }
    var tutorialLoaded by remember { mutableStateOf(false) }




    LaunchedEffect(criarGrupoPos) {
        if (criarGrupoPos != Offset.Zero && !tutorialLoaded) {
            val shown = TutorialPrefs.wasTutorialShown(context)
            println("DEBUG → Tutorial já mostrado? $shown")
            if (!shown) {
                delay(300)
                mostrarTutorial = true
                println("DEBUG → mostrarTutorial = true")
            }
            tutorialLoaded = true
        }
    }




    // === Carrega grupos ===
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

    // === Carrega áreas ===
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

    val gruposFiltrados = if (categoriaSelecionada == "Todas") grupos else grupos.filter {
        it.id_area == (areaSelecionadaObj?.id_area ?: -1)
    }

    // === Layout principal ===
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FF))
            .padding(16.dp)
    ) {
        Text("Bem-vindo ao Journey!", fontSize = 26.sp, color = Color.Black, fontWeight = FontWeight.Bold)
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
                Text("Grupos", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // === Botão Criar Grupo ===
                    Button(
                        onClick = { navegacao.navigate("criar_grupo") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.onGloballyPositioned { coords ->
                            if (criarGrupoPos == Offset.Zero) { // 👈 só pega a primeira vez
                                criarGrupoPos = coords.localToWindow(Offset.Zero)
                            }
                        }
                    ) {
                        Text("+ Criar Grupo", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }


                    // === Dropdown ===
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
                                categoriaSelecionada = "Todas"; expanded = false
                            })
                            areas.forEach { area ->
                                DropdownMenuItem(text = { Text(area.area) }, onClick = {
                                    categoriaSelecionada = area.area; expanded = false
                                })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(gruposFiltrados) { grupo ->
                        GrupoCard(
                            grupo = grupo,
                            modifier = Modifier.onGloballyPositioned { coords ->
                                if (cardPos == Offset.Zero) {
                                    cardPos = coords.localToWindow(Offset.Zero)
                                }
                            },
                            onClick = { navegacao.navigate("grupoinfo/${grupo.id_grupo}") }
                        )
                    }
                }
            }
        }
    }

    // === Overlay do tutorial ===
    if (mostrarTutorial) {
        val highlightOffset = when (tutorialStep) {
            0 -> criarGrupoPos
            1 -> cardPos
            else -> Offset.Zero
        }

        if (highlightOffset != Offset.Zero) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable {
                        tutorialStep++
                        if (tutorialStep > 1) {
                            mostrarTutorial = false
                            TutorialPrefs.saveTutorialShown(context)
                            println("DEBUG → Tutorial salvo no DataStore")
                        }
                    }
            ) {
                val infiniteTransition = rememberInfiniteTransition()
                val arrowOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Column(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = highlightOffset.x.toInt() + 50,
                                y = if (tutorialStep == 0)
                                    highlightOffset.y.toInt() - 200
                                else
                                    highlightOffset.y.toInt() + 150
                            )
                        }
                        .width(220.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier
                            .size(40.dp)
                            .rotate(if (tutorialStep == 0) -120f else 60f)
                            .offset(y = if (tutorialStep == 0) arrowOffset.dp else -arrowOffset.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (tutorialStep == 0)
                            "Clique neste botão para criar um grupo!"
                        else
                            "Agora clique em um grupo para ver mais detalhes!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}


