package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext

data class Evento(
    val id: Int,
    val data: LocalDate,
    val nome: String,
    val descricao: String,
    val hora: String?,
    val link: String,
    val grupoId: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(
    navController: NavHostController,
    grupoId: Int,
    idUsuario: Int
){
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
        println("🟣 Calendario iniciado - grupoId=$grupoId, idUsuario=$idUsuario")
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
                        }.filter { it.grupoId == grupoId }

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

        if (dataSelecionada != null) {
            ModalBottomSheet(
                onDismissRequest = { dataSelecionada = null },
                sheetState = sheetState,
            ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                ) {
                    Text(
                        text = "Eventos de ${dataSelecionada!!.dayOfMonth}/${dataSelecionada!!.monthValue}/${dataSelecionada!!.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val eventosDoDia = eventos.filter { it.data == dataSelecionada }
                    val context = LocalContext.current

                    if (eventosDoDia.isEmpty()) {
                        Text("Nenhum evento", color = Color.Gray)
                    } else {
                        eventosDoDia.forEach { evento ->
                            var mostrarConfirmacao by remember { mutableStateOf(false) }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("• ${evento.nome}", fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { mostrarConfirmacao = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir evento",
                                            tint = Color.Red
                                        )
                                    }
                                }

                                // Exibe os detalhes do evento
                                Text("Descrição: ${evento.descricao}", style = MaterialTheme.typography.bodySmall)
                                if (!evento.hora.isNullOrBlank())
                                    Text("Hora: ${evento.hora}", style = MaterialTheme.typography.bodySmall)
                                if (evento.link.isNotBlank())
                                    Text(
                                        "Link: ${evento.link}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF341E9B)
                                    )

                                Divider(color = Color.LightGray)
                            }

                            if (mostrarConfirmacao) {
                                AlertDialog(
                                    onDismissRequest = { mostrarConfirmacao = false },
                                    title = { Text("Excluir evento") },
                                    text = { Text("Tem certeza que deseja excluir este evento?") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            mostrarConfirmacao = false
                                            RetrofitInstance.calendarioService.listar().enqueue(object : Callback<CalendarioResponseWrapper> {
                                                override fun onResponse(
                                                    call: Call<CalendarioResponseWrapper>,
                                                    response: Response<CalendarioResponseWrapper>
                                                ) {
                                                    if (response.isSuccessful) {
                                                        val dados = response.body()
                                                        // use os dados aqui
                                                    }
                                                }

                                                override fun onFailure(call: Call<CalendarioResponseWrapper>, t: Throwable) {
                                                    // tratar erro
                                                    t.printStackTrace()
                                                }
                                            })

                                        }) { Text("Sim") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { mostrarConfirmacao = false }) {
                                            Text("Cancelar")
                                        }
                                    }
                                )
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Criar novo evento", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val cornerRadius = 32.dp

                    OutlinedTextField(
                        value = novoNome,
                        onValueChange = { novoNome = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(cornerRadius)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novaDescricao,
                        onValueChange = { novaDescricao = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(cornerRadius)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novaHora,
                        onValueChange = { novaHora = it },
                        label = { Text("Hora (HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(cornerRadius)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = novoLink,
                        onValueChange = { novoLink = it },
                        label = { Text("Link") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(cornerRadius)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (novoNome.isNotBlank() && novaDescricao.isNotBlank()) {
                            val dataHora = "${dataSelecionada}T$novaHora:00"
                            val novoEvento = NovoEventoRequest(
                                nome_evento = novoNome,
                                data_evento = dataHora,
                                descricao = novaDescricao,
                                link = novoLink,
                                id_grupo = grupoId,
                                id_usuario = idUsuario
                            )

                            RetrofitInstance.calendarioService.criarEvento(novoEvento)
                                .enqueue(object : retrofit2.Callback<CalendarioResponseWrapper> {
                                    override fun onResponse(
                                        call: retrofit2.Call<CalendarioResponseWrapper>,
                                        response: retrofit2.Response<CalendarioResponseWrapper>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Evento criado!", Toast.LENGTH_SHORT).show()

                                            // Cria evento localmente usando os dados enviados
                                            val novaData = dataSelecionada!!
                                            eventos = eventos + Evento(
                                                id = (eventos.maxOfOrNull { it.id } ?: 0) + 1,
                                                data = novaData,
                                                nome = novoNome,
                                                descricao = novaDescricao,
                                                hora = novaHora,
                                                link = novoLink,
                                                grupoId = grupoId
                                            )


                                            // Limpa campos
                                            novoNome = ""
                                            novaDescricao = ""
                                            novoLink = ""
                                            novaHora = ""
                                        } else {
                                            Toast.makeText(context, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: retrofit2.Call<CalendarioResponseWrapper>, t: Throwable) {
                                        Toast.makeText(context, "Erro ao criar evento: ${t.message}", Toast.LENGTH_LONG).show()
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