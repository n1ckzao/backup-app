package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White

data class Ebook(val titulo: String, val preco: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(onFinalizar: () -> Unit, onVoltar: () -> Unit) {
    // Lista de e-books no carrinho
    val ebooks = remember {
        listOf(
            Ebook("Rise Above", "R$ 50,00"),
            Ebook("Segredos do Sucesso", "R$ 60,00"),
            Ebook("Liberdade Financeira", "R$ 45,00")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Carrinho", color = White) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Meu Carrinho",
                fontSize = 20.sp,
                color = PrimaryPurple,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ebooks) { ebook ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(PrimaryPurple, shape = RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(ebook.titulo, fontSize = 16.sp, color = Color.Black)
                                Spacer(Modifier.height(4.dp))
                                Text(ebook.preco, fontSize = 14.sp, color = PrimaryPurple)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onFinalizar,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Finalizar Compra", color = White, fontSize = 16.sp)
            }
        }
    }
}