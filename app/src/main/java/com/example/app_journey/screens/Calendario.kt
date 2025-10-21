package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
                    val eventosDoDia = eventos.filter { it.data == dia }

                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable {
                                dataSelecionada = dia
                                mostrarDialogo = true
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (eventosDoDia.isNotEmpty()) Color(0xFFDAD5FF) else Color(0xFFEDEEFF)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Número do dia
                            Text(
                                text = dia.dayOfMonth.toString(),
                                fontWeight = if (dia == LocalDate.now()) FontWeight.Bold else FontWeight.Medium,
                                color = Color(0xFF2B1B84)
                            )

                            // Exibe os eventos (limitando a 2 para não poluir)
                            eventosDoDia.take(2).forEach { evento ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = evento.descricao,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2B1B84),
                                    maxLines = 2
                                )
                            }

                            // Indicador de mais eventos
                            if (eventosDoDia.size > 2) {
                                Text(
                                    text = "+${eventosDoDia.size - 2} mais",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }


        // Diálogo de novo evento
        if (mostrarDialogo && dataSelecionada != null) {
            NovoEventoDialog(
                data = dataSelecionada!!,
                grupoId = grupoId,
                onSalvar = { nome, descricao, dataHora, link ->
                    val novoEvento = NovoEventoRequest(
                        nome_evento = nome,
                        data_evento = dataHora,
                        descricao = descricao,
                        link = link,
                        id_grupo = grupoId
                    )

                    val service = RetrofitInstance.calendarioService
                    service.criarEvento(novoEvento).enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
                        override fun onResponse(
                            call: retrofit2.Call<CalendarioResponseWrapper>,
                            response: retrofit2.Response<CalendarioResponseWrapper>
                        ) {
                            println("🟢 Resposta: ${response.code()} ${response.message()}")
                            println("🟢 Body: ${response.body()}")
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
@Composable
fun NovoEventoDialog(
    data: LocalDate,
    grupoId: Int,
    onSalvar: (nome: String, descricao: String, dataHora: String, link: String) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("14:30") } // valor padrão

    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    println("🟢 Clicou em salvar! Nome=$nome, Desc=$descricao")
                    if (nome.isNotBlank() && descricao.isNotBlank()) {
                        val dataHora = "${data}T$hora:00"
                        onSalvar(nome, descricao, dataHora, link)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
        title = {
            Text(
                text = "Novo evento em ${data.dayOfMonth}/${data.monthValue}/${data.year}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do evento") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Link (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}





@Preview(showSystemUi = true)
@Composable
fun PreviewCalendario() {
    val fakeNav = rememberNavController()
    Calendario(navController = fakeNav, grupoId = 1)
}
