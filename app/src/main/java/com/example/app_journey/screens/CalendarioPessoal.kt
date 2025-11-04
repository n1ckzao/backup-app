package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.CalendarioResponseWrapper
import com.example.app_journey.model.NovoEventoRequest
import com.example.app_journey.service.RetrofitInstance
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioPessoal(
    navController: NavHostController,
    idUsuario: Int
) {
    val hoje = remember { LocalDate.now() }
    var mesAtual by remember { mutableStateOf(YearMonth.now()) }
    var eventos by remember { mutableStateOf(listOf<Evento>()) }
    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Carregar todos os eventos dos grupos do usuário
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
                                val hora = item.data_evento.substring(11, 16)
                                Evento(
                                    id = item.id_calendario,
                                    data = data,
                                    nome = item.nome_evento,
                                    descricao = item.descricao,
                                    hora = hora,
                                    link = item.link,
                                    grupoId = item.id_grupo
                                )
                            } catch (e: Exception) { null }
                        }
                    }
                } else {
                    Toast.makeText(context, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 🔹 Lógica de mês e dias do calendário
    val primeiroDiaDoMes = mesAtual.atDay(1)
    val diaSemanaInicio = primeiroDiaDoMes.dayOfWeek.value % 7
    val diasNoMes = mesAtual.lengthOfMonth()
    val diasCalendario = buildList<LocalDate?> {
        repeat(diaSemanaInicio) { add(null) }
        (1..diasNoMes).forEach { add(mesAtual.atDay(it)) }
    }

    // 🔹 UI
    Box {
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

            // Grade de dias
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
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dia.dayOfMonth.toString(),
                                    fontWeight = if (dia == hoje) FontWeight.Bold else FontWeight.Medium
                                )
                                eventosDoDia.take(2).forEach { evento ->
                                    Text(evento.nome, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
                                if (eventosDoDia.size > 2)
                                    Text("+${eventosDoDia.size - 2} mais", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // 🔹 BottomSheet com detalhes dos eventos do dia
        if (dataSelecionada != null) {
            ModalBottomSheet(
                onDismissRequest = { dataSelecionada = null },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Eventos de ${dataSelecionada!!.dayOfMonth}/${dataSelecionada!!.monthValue}/${dataSelecionada!!.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val eventosDoDia = eventos.filter { it.data == dataSelecionada }
                    if (eventosDoDia.isEmpty()) {
                        Text("Nenhum evento", color = Color.Gray)
                    } else {
                        eventosDoDia.forEach { evento ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text("• ${evento.nome}", fontWeight = FontWeight.Bold)
                                Text("Descrição: ${evento.descricao}", style = MaterialTheme.typography.bodySmall)
                                if (!evento.hora.isNullOrBlank())
                                    Text("Hora: ${evento.hora}", style = MaterialTheme.typography.bodySmall)
                                if (evento.link.isNotBlank())
                                    Text("Link: ${evento.link}", color = Color(0xFF341E9B))
                                Text("Grupo ID: ${evento.grupoId}", color = Color.Gray, fontSize = 12.sp)
                                Divider(color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
