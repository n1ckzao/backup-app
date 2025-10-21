package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(
    navController: NavHostController,
    grupoId: Int) {
    val hoje = remember { LocalDate.now() }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(listOf<Evento>()) }

    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Campos do novo evento
    var novoNome by remember { mutableStateOf("") }
    var novaDescricao by remember { mutableStateOf("") }
    var novoLink by remember { mutableStateOf("") }
    var novaHora by remember { mutableStateOf("") }

    // Buscar eventos do backend
    LaunchedEffect(Unit) {
        val service = RetrofitInstance.calendarioService
        service.getTodosEventos().enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
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
                            } catch (e: Exception) { null }
                        }.filter { it.grupoId == grupoId } // 🔥 filtra os eventos do grupo atual
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                println("Erro ao carregar eventos: ${t.message}")
            }
        })
    }


    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()
    val diasCalendario = buildList<LocalDate?> {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    Box {
        // 1️⃣ Corpo do calendário
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEEFF))
                .padding(16.dp)
        ) {
            // Cabeçalho mês
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) { Text("<") }
                Text(
                    text = "${mesAtual.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() }} ${mesAtual.year}",
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) { Text(">") }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dias da semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("D","S","T","Q","Q","S","S").forEach { dia ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(dia, fontWeight = FontWeight.Bold, color = Color(0xFF341E9B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grade do calendário
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(diasCalendario.size) { index ->
                    val dia = diasCalendario[index]
                    if (dia == null) Box(modifier = Modifier.aspectRatio(1f)) {}
                    else {
                        val eventosDoDia = eventos.filter { it.data == dia }
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    dataSelecionada = dia
                                    coroutineScope.launch { sheetState.show() }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (eventosDoDia.isNotEmpty()) Color(0xFFDAD5FF) else Color(0xFFEDEEFF)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dia.dayOfMonth.toString(),
                                    fontWeight = if (dia == hoje) FontWeight.Bold else FontWeight.Medium
                                )
                                eventosDoDia.take(2).forEach { evento ->
                                    Text(evento.descricao, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                }
                                if (eventosDoDia.size > 2)
                                    Text("+${eventosDoDia.size - 2} mais", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // 2️⃣ Bottom sheet que abre somente ao clicar no dia
        if (dataSelecionada != null) {
            ModalBottomSheet(
                onDismissRequest = { dataSelecionada = null },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Eventos de ${dataSelecionada!!.dayOfMonth}/${dataSelecionada!!.monthValue}/${dataSelecionada!!.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val eventosDoDia = eventos.filter { it.data == dataSelecionada }
                    if (eventosDoDia.isEmpty()) Text("Nenhum evento", color = Color.Gray)
                    else eventosDoDia.forEach { Text("- ${it.descricao}") }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Criar novo evento", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(value = novoNome, onValueChange = { novoNome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = novaDescricao, onValueChange = { novaDescricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = novaHora, onValueChange = { novaHora = it }, label = { Text("Hora (HH:mm)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = novoLink, onValueChange = { novoLink = it }, label = { Text("Link (opcional)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (novoNome.isNotBlank() && novaDescricao.isNotBlank()) {
                            val dataHora = "${dataSelecionada}T$novaHora:00"
                            val novoEvento = NovoEventoRequest(novoNome, dataHora, novaDescricao, novoLink, grupoId)

                            RetrofitInstance.calendarioService.criarEvento(novoEvento)
                                .enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
                                    override fun onResponse(
                                        call: retrofit2.Call<CalendarioResponseWrapper>,
                                        response: retrofit2.Response<CalendarioResponseWrapper>
                                    ) {
                                        if (response.isSuccessful) {
                                            println("✅ Evento criado com sucesso no backend")

                                            // Atualiza localmente
                                            eventos = eventos + Evento(
                                                dataSelecionada!!,
                                                novaDescricao,
                                                novoLink,
                                                grupoId
                                            )

                                            // Limpa campos
                                            novoNome = ""
                                            novaDescricao = ""
                                            novoLink = ""
                                            novaHora = ""
                                        } else {
                                            println("❌ Erro no backend: ${response.code()} ${response.message()}")
                                        }
                                    }

                                    override fun onFailure(
                                        call: retrofit2.Call<CalendarioResponseWrapper>,
                                        t: Throwable
                                    ) {
                                        println("⚠️ Falha ao conectar ao backend: ${t.message}")
                                    }
                                })
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Salvar evento")
                    }


                }
            }
        }
    }
}