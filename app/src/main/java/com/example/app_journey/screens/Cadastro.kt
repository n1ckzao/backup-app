package com.example.app_journey.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Cadastro(navegacao: NavHostController) {
    val nome_completo = remember { mutableStateOf("") }
    val dataNascimento = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val confirmarSenha = remember { mutableStateOf("") }
    val context = LocalContext.current

    // 👉 Formata a data automaticamente no formato dd/MM/yyyy
    fun formatarDataExibicao(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length <= 2 -> digits
            digits.length <= 4 -> "${digits.take(2)}/${digits.drop(2)}"
            digits.length <= 8 -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4)}"
            else -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4).take(4)}"
        }
    }

    // 👉 Converte dd/MM/yyyy para yyyy-MM-dd (para enviar ao backend)
    fun formatarDataParaIso(data: String): String {
        return try {
            val partes = data.split("/")
            val dia = partes[0]
            val mes = partes[1]
            val ano = partes[2]
            "$ano-$mes-$dia"
        } catch (e: Exception) {
            data // se der erro, envia como está
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xff39249D), Color(0xff180D5B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .height(580.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xff351D9B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ) {

                    Text(
                        "Crie sua conta",
                        fontSize = 35.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Já tem uma conta?", fontSize = 18.sp, color = Color.White)
                        Button(
                            modifier = Modifier.height(45.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            onClick = { navegacao.navigate(route = "login") }
                        ) {
                            Text(
                                "Log in",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight(1000)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Nome
                        OutlinedTextField(
                            value = nome_completo.value,
                            onValueChange = { nome_completo.value = it },
                            label = { Text(text = "Nome", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier
                                .height(55.dp)
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        // Data de nascimento (com máscara)
                        OutlinedTextField(
                            value = dataNascimento.value,
                            onValueChange = { input ->
                                dataNascimento.value = formatarDataExibicao(input)
                            },
                            label = { Text(text = "Data de Nascimento (dd/mm/aaaa)", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier
                                .height(55.dp)
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        // Email
                        OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            label = { Text(text = "E-mail", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier
                                .height(55.dp)
                                .fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        // Senhas
                        OutlinedTextField(
                            value = senha.value,
                            onValueChange = { senha.value = it },
                            label = { Text(text = "Senha", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier.height(55.dp).fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        OutlinedTextField(
                            value = confirmarSenha.value,
                            onValueChange = { confirmarSenha.value = it },
                            label = { Text(text = "Confirmar senha", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier.height(55.dp).fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (nome_completo.value.isBlank() || email.value.isBlank() || senha.value.isBlank()) {
                                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val dataIso = formatarDataParaIso(dataNascimento.value)

                                val usuario = Usuario(
                                    nome_completo = nome_completo.value,
                                    data_nascimento = dataIso,
                                    email = email.value,
                                    senha = senha.value,
                                    tipo_usuario = "Estudante",
                                    id_usuario = 1,
                                    foto_perfil = "",
                                    descricao = ""
                                )

                                Log.d("Cadastro", "Enviando usuário: $usuario")

                                RetrofitFactory().getUsuarioService().inserirUsuario(usuario)
                                    .enqueue(object : Callback<Usuario> {
                                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                                navegacao.navigate("login")
                                            } else {
                                                val erroMsg = response.errorBody()?.string()
                                                Toast.makeText(context, "Erro ao cadastrar: $erroMsg", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                            Toast.makeText(context, "Erro na conexão com o servidor.", Toast.LENGTH_LONG).show()
                                            Log.e("API", "Falha: ${t.message}")
                                        }
                                    })
                            },
                            shape = RoundedCornerShape(48.dp),
                            modifier = Modifier
                                .width(250.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Cadastrar", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xff341E9B))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CadastroPreview() {
    val navController = rememberNavController()
    Cadastro(navegacao = navController)
}
