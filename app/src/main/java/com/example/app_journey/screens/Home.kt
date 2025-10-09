package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
@Composable
fun Home(navegacao: NavHostController) {
    val grupos = remember { mutableStateListOf<Grupo>() }
    val context = LocalContext.current

    // GET grupos ao abrir tela
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

    // Estrutura da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FF)) // Fundo claro igual ao app
            .padding(16.dp)
    ) {
        // Título principal
        Text(
            text = "Bem-vindo ao Journey!",
            fontSize = 26.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Uma plataforma para mentoria e\naprendizado colaborativo",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card roxo com "Grupos", botões e lista
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF341E9B)), // Roxo
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Grupos",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botão Criar Grupo
                    Button(
                        onClick = { navegacao?.navigate("criargrupo") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("+ Criar Grupo", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }

                    // Botão Categoria (apenas visual por enquanto)
                    Button(
                        onClick = { /* TODO: abrir filtro */ },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("✔ Categoria", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de cards de grupos
                LazyColumn {
                    items(grupos) { grupo ->
                        GrupoCard(grupo)
                    }
                }
            }
        }
    }
}
