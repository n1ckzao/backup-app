package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import com.example.app_journey.R
import com.example.app_journey.model.LoginRequest
import com.example.app_journey.model.LoginResponse
import com.example.app_journey.model.UsuarioResponse
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//@Composable
//fun Login(navegacao: NavHostController?) {
//    val email = remember { mutableStateOf("") }
//    val senha = remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val erro = remember { mutableStateOf<String?>(null) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.linearGradient(
//                    colors = listOf(Color(0xff39249D), Color(0xff180D5B))
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(start = 16.dp, end = 16.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Card(
//                modifier = Modifier
//                    .height(560.dp)
//                    .fillMaxWidth(),
//                colors = CardDefaults.cardColors(containerColor = Color(0xff351D9B))
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(15.dp),
//                    verticalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.logo),
//                        contentDescription = "",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                    )
//
//                    Column(modifier = Modifier.fillMaxWidth().height(226.dp)) {
//                        Text(
//                            text = "Login",
//                            fontSize = 27.sp,
//                            color = Color.White,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.Center,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(
//                                text = "Não possui uma conta?",
//                                fontSize = 15.sp,
//                                color = Color.White
//                            )
//                            Button(
//                                modifier = Modifier.height(35.dp),
//                                colors = ButtonDefaults.buttonColors(Color.Transparent),
//                                onClick = { navegacao?.navigate("cadastro") }
//                            ) {
//                                Text(
//                                    text = buildAnnotatedString {
//                                        withStyle(
//                                            style = SpanStyle(
//                                                textDecoration = TextDecoration.Underline
//                                            )
//                                        ) {
//                                            append("Cadastrar")
//                                        }
//                                    },
//                                    fontSize = 14.sp,
//                                    color = Color.White
//                                )
//                            }
//                        }
//
//                        OutlinedTextField(
//                            value = email.value,
//                            onValueChange = { email.value = it },
//                            label = { Text(text = "Email", color = Color.White) },
//                            shape = RoundedCornerShape(33.dp),
//                            singleLine = true,
//                            modifier = Modifier.height(57.dp).fillMaxWidth(),
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Email,
//                                imeAction = ImeAction.Next
//                            ),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                cursorColor = Color.White,
//                                focusedBorderColor = Color.White,
//                                unfocusedBorderColor = Color.Gray,
//                                focusedLabelColor = Color.White,
//                                unfocusedLabelColor = Color.Gray,
//                                unfocusedContainerColor = Color.Transparent,
//                                focusedContainerColor = Color.Transparent
//                            )
//                        )
//
//                        Spacer(modifier = Modifier.height(25.dp))
//
//                        OutlinedTextField(
//                            value = senha.value,
//                            onValueChange = { senha.value = it },
//                            label = { Text(text = "Senha", color = Color.White) },
//                            shape = RoundedCornerShape(33.dp),
//                            singleLine = true,
//                            modifier = Modifier.height(57.dp).fillMaxWidth(),
//                            keyboardOptions = KeyboardOptions(
//                                keyboardType = KeyboardType.Password,
//                                imeAction = ImeAction.Done
//                            ),
//                            visualTransformation = PasswordVisualTransformation(),
//                            colors = OutlinedTextFieldDefaults.colors(
//                                focusedTextColor = Color.White,
//                                unfocusedTextColor = Color.White,
//                                cursorColor = Color.White,
//                                focusedBorderColor = Color.White,
//                                unfocusedBorderColor = Color.Gray,
//                                focusedLabelColor = Color.White,
//                                unfocusedLabelColor = Color.Gray,
//                                unfocusedContainerColor = Color.Transparent,
//                                focusedContainerColor = Color.Transparent
//                            )
//                        )
//
//                        erro.value?.let { mensagemErro ->
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(text = mensagemErro, color = Color.Red)
//                        }
//                    }
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(150.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Button(
//                            onClick = { navegacao?.navigate("recuperacao_senha") },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
//                            modifier = Modifier.height(35.dp),
//                            shape = RoundedCornerShape(0.dp)
//                        ) {
//                            Text(
//                                text = buildAnnotatedString {
//                                    withStyle(
//                                        style = SpanStyle(textDecoration = TextDecoration.Underline)
//                                    ) {
//                                        append("Esqueci minha senha")
//                                    }
//                                },
//                                fontSize = 14.sp,
//                                color = Color.White
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Button(
//                            onClick = {
//                                if (email.value.isBlank() || senha.value.isBlank()) {
//                                    erro.value = "Preencha todos os campos"
//                                    return@Button
//                                }
//
//                                val usuarioService = RetrofitFactory().getUsuarioService()
//                                val loginRequest = LoginRequest(email.value, senha.value)
//
//                                usuarioService.loginUsuario(loginRequest)
//                                    .enqueue(object : Callback<LoginResponse> {
//                                        override fun onResponse(
//                                            call: Call<LoginResponse>,
//                                            response: Response<LoginResponse>
//                                        ) {
//                                            if (response.isSuccessful) {
//                                                val loginResponse = response.body()
//                                                if (loginResponse != null && loginResponse.status) {
//                                                    erro.value = "Login realizado com sucesso"
//
//                                                    // salva dados no SharedPreferences
//                                                    loginResponse.usuario?.let { usuario ->
//                                                        SharedPrefHelper.salvarUsuario(
//                                                            context,
//                                                            usuario
//                                                        )
//                                                        SharedPrefHelper.salvarIdUsuario(
//                                                            context,
//                                                            usuario.id
//                                                        )
//                                                        SharedPrefHelper.salvarEmail(
//                                                            context,
//                                                            usuario.email
//                                                        )
//                                                        Log.e("Login", "ID: ${usuario.id}")
//                                                    }
//
//                                                    navegacao?.navigate("home/${SharedPrefHelper.recuperarIdUsuario(context)}") {
//                                                        popUpTo("login") { inclusive = true }
//                                                    }
//
//                                                } else {
//                                                    erro.value =
//                                                        loginResponse?.message
//                                                            ?: "Email ou senha incorretos"
//                                                }
//                                            } else {
//                                                erro.value = "Erro ao fazer login: ${response.code()}"
//                                            }
//                                        }
//
//                                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                                            erro.value = "Erro de rede: ${t.message}"
//                                        }
//                                    })
//                            },
//                            shape = RoundedCornerShape(48.dp),
//                            modifier = Modifier
//                                .width(250.dp)
//                                .height(40.dp),
//                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
//                        ) {
//                            Text(
//                                text = "Login",
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.ExtraBold,
//                                color = Color(0xff341E9B)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun LoginPreview() {
//    Login(navegacao = null)
//}
@Composable
fun Login(navegacao: NavHostController?) {
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val context = LocalContext.current
    val erro = remember { mutableStateOf<String?>(null) }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Logo
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = "Login",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    // Mensagem de cadastro
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Não possui uma conta? ",
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        TextButton(onClick = { navegacao?.navigate("cadastro") }) {
                            Text(
                                text = "Cadastrar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEFEAEF),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }

                    // Campos de texto
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("Email", color = Color.White) },
                        singleLine = true,
                        shape = RoundedCornerShape(33.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = senha.value,
                        onValueChange = { senha.value = it },
                        label = { Text("Senha", color = Color.White) },
                        singleLine = true,
                        shape = RoundedCornerShape(33.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color(0xFFFFFFFF),
                            unfocusedBorderColor = Color.Gray
                        )
                    )

                    erro.value?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }

                    // Recuperar senha
                    TextButton(onClick = { navegacao?.navigate("recuperacao_senha") }) {
                        Text(
                            "Esqueci minha senha",
                            color = Color(0xFFEFEAEF),
                            textDecoration = TextDecoration.Underline,
                            fontSize = 14.sp
                        )
                    }

                    // Botão Login
                    Button(
                        onClick = {
                            if (email.value.isBlank() || senha.value.isBlank()) {
                                erro.value = "Preencha todos os campos"
                                return@Button
                            }

                            val usuarioService = RetrofitFactory().getUsuarioService()
                            val loginRequest = LoginRequest(email.value, senha.value)

                            usuarioService.loginUsuario(loginRequest)
                                .enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(
                                        call: Call<LoginResponse>,
                                        response: Response<LoginResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            val loginResponse = response.body()
                                            if (loginResponse != null && loginResponse.status) {
                                                erro.value = "Login realizado com sucesso"

                                                // salva dados no SharedPreferences
                                                loginResponse.usuario?.let { usuario ->
                                                    SharedPrefHelper.salvarUsuario(
                                                        context,
                                                        usuario
                                                    )
                                                    SharedPrefHelper.salvarIdUsuario(
                                                        context,
                                                        usuario.id
                                                    )
                                                    SharedPrefHelper.salvarEmail(
                                                        context,
                                                        usuario.email
                                                    )
                                                    Log.e("Login", "ID: ${usuario.id}")
                                                }

                                                navegacao?.navigate("home/${SharedPrefHelper.recuperarIdUsuario(context)}") {
                                                    popUpTo("login") { inclusive = true }
                                                }

                                            } else {
                                                erro.value =
                                                    loginResponse?.message
                                                        ?: "Email ou senha incorretos"
                                            }
                                        } else {
                                            erro.value = "Erro ao fazer login: ${response.code()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        erro.value = "Erro de rede: ${t.message}"
                                    }
                                })
                        },
                        shape = RoundedCornerShape(48.dp),
                        modifier = Modifier
                            .width(250.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xff341E9B)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginPreview() {
    Login(navegacao = null)
}