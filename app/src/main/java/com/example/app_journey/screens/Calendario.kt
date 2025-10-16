package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

data class Evento(
    val data: LocalDate,
    val descricao: String,
    val link: String,
    val grupoId: Int
)

@Composable
fun Calendario(
    navController: NavHostController,
    grupoId: Int
) {
    val hoje = remember { LocalDate.now() }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(listOf<Evento>()) }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }

    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()

    val diasCalendario = buildList {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    // 🔹 Passo 4: buscar eventos do grupo no backend
    LaunchedEffect(grupoId) {
        val service = RetrofitInstance.calendarioService
        service.getEventosPorGrupo(grupoId).enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
            override fun onResponse(
                call: retrofit2.Call<CalendarioResponseWrapper>,
                response: retrofit2.Response<CalendarioResponseWrapper>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == true && body.Calendario != null) {
                        eventos = body.Calendario.mapNotNull { item ->
                            try {
                                val data = LocalDate.parse(item.data_evento.substring(0, 10))
                                Evento(
                                    data = data,
                                    descricao = item.descricao,
                                    link = item.link,
                                    grupoId = item.id_grupo
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                println("Erro ao carregar eventos: ${t.message}")
            }
        })
    }

    val diasSemana = DayOfWeek.values()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEEFF))
            .padding(16.dp)
    ) {
        // Cabeçalho do mês
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                Text("<", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }
            Text(
                text = mesAtual.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() } + " ${mesAtual.year}",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                Text(">", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cabeçalho dos dias da semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("D", "S", "T", "Q", "Q", "S", "S").forEach {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF341E9B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grade do calendário
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diasCalendario.size) { index ->
                val dia = diasCalendario[index]
                if (dia == null) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(Color.Transparent)
                    )
                } else {
                    val eventoDia = eventos.find { it.data == dia }
                    val temEvento = eventoDia != null

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                if (temEvento) Color(0xFFB1A6FF) else Color(0xFFE1E3FF),
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable {
                                dataSelecionada = dia
                                mostrarDialogo = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dia.dayOfMonth.toString(),
                            color = if (temEvento) Color.White else Color.Black,
                            fontWeight = if (dia == hoje) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Diálogo de novo evento
        if (mostrarDialogo && dataSelecionada != null) {
            NovoEventoDialog(
                data = dataSelecionada!!,
                grupoId = grupoId,
                onSalvar = { descricao, link ->
                    // 🔹 Quando clicar em "Salvar", cria evento via API
                    val service = RetrofitInstance.calendarioService
                    val novoEvento = NovoEventoRequest(
                        nome_evento = descricao,
                        data_evento = "${dataSelecionada.toString()}T00:00:00",
                        descricao = descricao,
                        link = link,
                        id_grupo = grupoId
                    )

                    service.criarEvento(novoEvento).enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
                        override fun onResponse(
                            call: retrofit2.Call<CalendarioResponseWrapper>,
                            response: retrofit2.Response<CalendarioResponseWrapper>
                        ) {
                            if (response.isSuccessful) {
                                eventos = eventos + Evento(
                                    data = dataSelecionada!!,
                                    descricao = descricao,
                                    link = link,
                                    grupoId = grupoId
                                )
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                            println("Erro ao criar evento: ${t.message}")
                        }
                    })

                    mostrarDialogo = false
                },
                onCancelar = { mostrarDialogo = false }
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewCalendario() {
    val fakeNav = rememberNavController()
    Calendario(navController = fakeNav, grupoId = 1)
}
