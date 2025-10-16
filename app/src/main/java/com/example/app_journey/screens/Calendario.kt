package com.example.app_journey.screens

import android.widget.Toast
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

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

    // Controle de diálogo para novo evento
    var mostrarDialogo by remember { mutableStateOf(false) }
    var dataSelecionada by remember { mutableStateOf<LocalDate?>(null) }

    val diasMes = mesAtual
        .atEndOfMonth()
        .dayOfMonth
        .let { dias -> (1..dias).map { mesAtual.atDay(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEEFF))
            .padding(16.dp)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mesAtual = mesAtual.minusMonths(1) }) {
                Text("<", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }
            Text(
                text = mesAtual.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
            IconButton(onClick = { mesAtual = mesAtual.plusMonths(1) }) {
                Text(">", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid de dias
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diasMes) { dia ->
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

        // Diálogo de criação de evento
        if (mostrarDialogo && dataSelecionada != null) {
            NovoEventoDialog(
                data = dataSelecionada!!,
                grupoId = grupoId,
                onSalvar = { descricao, link ->
                    val novo = Evento(
                        data = dataSelecionada!!,
                        descricao = descricao,
                        link = link,
                        grupoId = grupoId
                    )
                    eventos = eventos + novo
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
    onSalvar: (descricao: String, link: String) -> Unit,
    onCancelar: () -> Unit
) {
    var descricao by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            Button(
                onClick = {
                    if (descricao.isNotBlank()) {
                        onSalvar(descricao, link)
                    }
                }
            ) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
        title = { Text("Novo evento em ${data.dayOfMonth}/${data.monthValue}") },
        text = {
            Column {
                Text("Grupo ID: $grupoId", color = Color.Gray, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Link da reunião (opcional)") },
                    singleLine = true,
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
