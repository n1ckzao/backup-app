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

    fun formatarDataExibicao(input: String): String {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length <= 2 -> digits
            digits.length <= 4 -> "${digits.take(2)}/${digits.drop(2)}"
            digits.length <= 8 -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4)}"
            else -> "${digits.take(2)}/${digits.drop(2).take(2)}/${digits.drop(4).take(4)}"
        }
    }

    fun formatarDataParaIso(data: String): String {
        return try {
            val partes = data.split("/")
            "${partes[2]}-${partes[1]}-${partes[0]}"
        } catch (e: Exception) {
            data
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF39249D), Color(0xFF180D5B))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF341E9B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Crie sua conta",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Já tem uma conta?", color = Color.White, fontSize = 16.sp)
                        TextButton(onClick = { navegacao.navigate("login") }) {
                            Text(
                                "Log in",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Campos
                    val camposModifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)

                    @Composable
                    fun outlinedColors() = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )

                    OutlinedTextField(
                        value = nome_completo.value,
                        onValueChange = { nome_completo.value = it },
                        label = { Text("Nome", color = Color.White) },
                        shape = RoundedCornerShape(33.dp),
                        singleLine = true,
                        modifier = camposModifier,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        colors = outlinedColors()
                    )

                    OutlinedTextField(
                        value = dataNascimento.value,
                        onValueChange = { input -> dataNascimento.value = formatarDataExibicao(input) },
                        label = { Text("Data de Nascimento (dd/MM/aaaa)", color = Color.White) },
                        shape = RoundedCornerShape(33.dp),
                        singleLine = true,
                        modifier = camposModifier,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        colors = outlinedColors()
                    )

                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("E-mail", color = Color.White) },
                        shape = RoundedCornerShape(33.dp),
                        singleLine = true,
                        modifier = camposModifier,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        colors = outlinedColors()
                    )

                    OutlinedTextField(
                        value = senha.value,
                        onValueChange = { senha.value = it },
                        label = { Text("Senha", color = Color.White) },
                        shape = RoundedCornerShape(33.dp),
                        singleLine = true,
                        modifier = camposModifier,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        colors = outlinedColors()
                    )

                    OutlinedTextField(
                        value = confirmarSenha.value,
                        onValueChange = { confirmarSenha.value = it },
                        label = { Text("Confirmar senha", color = Color.White) },
                        shape = RoundedCornerShape(33.dp),
                        singleLine = true,
                        modifier = camposModifier,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        colors = outlinedColors()
                    )

                    // Botão Cadastrar
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
                                foto_perfil = "",
                                descricao = ""
                            )

                            RetrofitFactory().getUsuarioService().inserirUsuario(usuario)
                                .enqueue(object : Callback<Usuario> {
                                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                            navegacao.navigate("login")
                                        } else {
                                            val erroMsg = response.errorBody()?.string()
                                            Toast.makeText(context, "Erro: $erroMsg", Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                        Toast.makeText(context, "Erro de rede.", Toast.LENGTH_LONG).show()
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Text("Cadastrar", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CadastroPreview() {
    val navController = rememberNavController()
    Cadastro(navegacao = navController)
}

