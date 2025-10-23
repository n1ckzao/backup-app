package com.example.app_journey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.PrimaryPurple
import com.example.app_journey.ui.theme.White
import androidx.compose.material.icons.filled.ShoppingCart


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEbooksScreen(
    onEbookClick: (Int) -> Unit,
    onCriarClick: () -> Unit,
    onCarrinhoClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journey E-books") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple),
                actions = {
                    IconButton(onClick = onCriarClick) {
                        Icon(Icons.Default.Add, contentDescription = "Criar", tint = White)
                    }
                    IconButton(onClick = onCarrinhoClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrinho", tint = White)
                    }
                }
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
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Pesquisar e-book") },
                modifier = Modifier.fillMaxWidth(0.9f), // centralizado e responsivo
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(6) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEbookClick(index) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F1FF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp), // altura do card
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(PrimaryPurple, shape = RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("E-book $index", fontSize = 16.sp, color = Color.Black)
                                Text("R$ ${index * 10},00", color = PrimaryPurple, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}