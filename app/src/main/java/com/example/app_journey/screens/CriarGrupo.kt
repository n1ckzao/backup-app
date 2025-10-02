package com.example.app_journey.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GrupoResult
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Criar Grupo", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Grupo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = id_area,
            onValueChange = { id_area = it },
            label = { Text("Área") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = limite,
            onValueChange = { limite = it.filter { c -> c.isDigit() } },
            label = { Text("Limite de Membros") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imagem,
            onValueChange = { imagem = it },
            label = { Text("URL da Imagem") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (nome.isBlank() || id_area.isBlank() || limite.isBlank() || descricao.isBlank() || imagem.isBlank()) {
                    mensagem = "Preencha todos os campos"
                    return@Button
                }

                val grupoService = RetrofitFactory().getGrupoService()
                val novoGrupo = Grupo(
                    nome = nome,
                    limite_membros = limite.toInt(),
                    descricao = descricao,
                    imagem = imagem,
                    id_area = id_area,
                    id_usuario = id_usuario
                )

                grupoService.inserirGrupo(novoGrupo).enqueue(object : Callback<GrupoResult> {
                    override fun onResponse(call: Call<GrupoResult>, response: Response<GrupoResult>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(context, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                            navegacao.navigate("home")
                        } else {
                            mensagem = "Erro ao criar grupo: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<GrupoResult>, t: Throwable) {
                        mensagem = "Erro de rede: ${t.message}"
                    }
                })


            },
            shape = RoundedCornerShape(48.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff341E9B))
        ) {
            Text("Criar Grupo", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (mensagem.isNotEmpty()) {
            Text(mensagem, color = Color.Red, fontSize = 14.sp)
        }
    }
}



@Preview
@Composable
private fun Preview() {
    val fakeNav = rememberNavController()
    CriarGrupo(navegacao = fakeNav)
}