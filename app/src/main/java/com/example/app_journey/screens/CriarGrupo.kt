package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.GruposResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CriarGrupo(navegacao: NavHostController) {
    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var id_area by remember { mutableStateOf("") }
    var limite by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var imagem by remember { mutableStateOf("") }
    val id_usuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    var mensagem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFD9DCFC))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Crie Seu Grupo no JOURNEY!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF4D35BC),
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF351D9B))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Nome do Grupo
                Text("Nome do Grupo:", color = Color.White, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = { Text("Digite o Nome do Grupo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Área
                Text("Área Específica:", color = Color.White, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = id_area,
                    onValueChange = { id_area = it },
                    placeholder = { Text("Digite a categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Limite de Membros
                Text("Limite de Membros:", color = Color.White, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = limite,
                    onValueChange = { limite = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("Máximo 30 Participantes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Descrição
                Text("Descrição:", color = Color.White, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Digite a descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Imagem
                Text("Enviar foto:", color = Color.White, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = imagem,
                    onValueChange = { imagem = it },
                    placeholder = { Text("URL da imagem") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.LightGray,
                        unfocusedPlaceholderColor = Color.LightGray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botão criar
                Button(
                    onClick = {
                        if (nome.isBlank() || id_area.isBlank() || limite.isBlank() || descricao.isBlank() || imagem.isBlank()) {
                            mensagem = "Preencha todos os campos"
                            return@Button
                        }

                        val grupoService = RetrofitFactory().getGrupoService()
                        val novoGrupo = com.example.app_journey.model.Grupo(
                            nome = nome,
                            limite_membros = limite.toInt(),
                            descricao = descricao,
                            imagem = imagem,
                            id_area = id_area,
                            id_usuario = id_usuario
                        )

                        grupoService.inserirGrupo(novoGrupo).enqueue(object : Callback<GruposResult> {
                            override fun onResponse(
                                call: Call<GruposResult>,
                                response: Response<GruposResult>
                            ) {
                                if (response.isSuccessful && response.body()?.status == true) {
                                    Toast.makeText(context, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                                    navegacao.navigate("home")
                                } else {
                                    mensagem = "Erro ao criar grupo: ${response.code()}"
                                }
                            }

                            override fun onFailure(call: Call<com.example.app_journey.model.GruposResult>, t: Throwable) {
                                mensagem = "Erro de rede: ${t.message}"
                            }
                        })
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8BDFA))
                ) {
                    Text(
                        "Criar Grupo",
                        color = Color(0xFF341E9B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (mensagem.isNotEmpty()) {
                    Text(mensagem, color = Color.Red, fontSize = 14.sp)
                }
            }
        }
    }
}




@Preview
@Composable
private fun Preview() {
    val fakeNav = rememberNavController()
    CriarGrupo(navegacao = fakeNav)
}