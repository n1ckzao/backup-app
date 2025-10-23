package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheEbookScreen(onAdicionarCarrinho: () -> Unit, onVoltar: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do e-book") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(PrimaryPurple, shape = RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.height(16.dp))
            Text("Liberdade Financeira", fontSize = 22.sp, color = PrimaryPurple)
            Spacer(Modifier.height(8.dp))
            Text("Categoria: Finanças", color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onAdicionarCarrinho,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Adicionar ao carrinho")
            }
        }
    }
}
