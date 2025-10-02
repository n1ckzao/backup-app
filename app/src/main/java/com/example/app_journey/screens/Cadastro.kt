package com.example.app_journey.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitFactory

@Composable
fun Cadastro(navegacao: NavHostController) {
    val nome_completo = remember { mutableStateOf("") }
    val dataNascimento = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val confirmarSenha = remember { mutableStateOf("") }
    val context = LocalContext.current

    fun formatarDataParaIso(data: String): String {
        return try {
            when {
                data.contains("/") -> {
                    // Entrada: "19/04/2008"
                    val partes = data.split("/")
                    val dia = partes[0]
                    val mes = partes[1]
                    val ano = partes[2]
                    "$ano-$mes-$dia"
                }

                data.length == 8 -> {
                    // Entrada: "19042008"
                    val dia = data.substring(0, 2)
                    val mes = data.substring(2, 4)
                    val ano = data.substring(4, 8)
                    "$ano-$mes-$dia"
                }

                else -> data // Retorna como está se o formato for desconhecido
            }
        } catch (e: Exception) {
            data // Retorna original em caso de erro
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(
                colors = listOf(Color(0xff39249D), Color(0xff180D5B))
            ))
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

                    Text("Crie sua conta",
                        fontSize = 35.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(),
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
                            Text("Log in", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight(1000))
                        }
                    }

                    Column(modifier = Modifier.fillMaxWidth().height(350.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        OutlinedTextField(
                            value = nome_completo.value,
                            onValueChange = { nome_completo.value = it },
                            label = { Text(text = "Nome", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier.height(55.dp).fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dataNascimento.value,
                            onValueChange = { dataNascimento.value = it },
                            label = { Text(text = "Data de Nascimento", color = Color.White) },
                            shape = RoundedCornerShape(33.dp),
                            singleLine = true,
                            modifier = Modifier
                                .height(55.dp).fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            shape = RoundedCornerShape(33.dp),
                            label = { Text(text = "E-mail", color = Color.White) },
                            singleLine = true,
                            modifier = Modifier.height(55.dp).fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = senha.value,
                            onValueChange = { senha.value = it },
                            shape = RoundedCornerShape(33.dp),
                            label = { Text(text = "Senha", color = Color.White) },
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

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmarSenha.value,
                            onValueChange = { confirmarSenha.value = it },
                            shape = RoundedCornerShape(33.dp),
                            label = { Text(text = "Confirmar senha", color = Color.White) },
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

                        Spacer(modifier = Modifier.height(22.dp))

                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                // Validação mínima
                                if (nome_completo.value.isBlank() || email.value.isBlank() || senha.value.isBlank()) {
                                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val usuario = Usuario(
                                    nome_completo = nome_completo.value,
                                    data_nascimento = dataNascimento.value,
                                    email = email.value,
                                    senha = senha.value,
                                    tipo_usuario = "Estudante",
                                    id_usuario = 1,
                                    foto_perfil = "",
                                    descricao = ""
                                )
                                Log.d("#####", "#######  ${usuario}   ################################")


                                val call = RetrofitFactory().getUsuarioService().inserirUsuario(usuario)

                                call.enqueue(object : Callback<Usuario> {
                                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                            navegacao.navigate(route = "login")
                                        } else {
                                            val erroMsg = response.errorBody()?.string()
                                            val mensagem = if (erroMsg?.contains("email", ignoreCase = true) == true) {
                                                "Este e-mail já está cadastrado."
                                            } else {
                                                "Erro ao cadastrar. Tente novamente."
                                            }
                                            Log.e("API", "Erro ao cadastrar: código ${response.code()}, corpo: $erroMsg")
                                            Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                        Log.e("API", "Falha na requisição: ${t.message}", t)
                                        Toast.makeText(context, "Erro na conexão com o servidor.", Toast.LENGTH_LONG).show()
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


                        Spacer(modifier = Modifier.height(10.dp))
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