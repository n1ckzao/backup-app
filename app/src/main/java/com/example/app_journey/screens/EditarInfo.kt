package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarInfo(
    navController: NavController,
    usuario: Usuario,
    onSave: (Usuario) -> Unit
) {
    var nome by remember { mutableStateOf(usuario.nome_completo) }
    var email by remember { mutableStateOf(usuario.email) }
    var dataNascimento by remember { mutableStateOf(usuario.data_nascimento ?: "") }
    var fotoPerfil by remember { mutableStateOf(usuario.foto_perfil ?: "") }
    var descricao by remember { mutableStateOf(usuario.descricao ?: "") }
    var senha by remember { mutableStateOf(usuario.senha) }
    var tipoUsuario by remember { mutableStateOf(usuario.tipo_usuario) }

    val outlinedColors = OutlinedTextFieldDefaults.colors(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Editar Perfil",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card com foto e nome
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PurpleDarker)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (fotoPerfil.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(fotoPerfil),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PurpleLighter),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nome.firstOrNull()?.toString() ?: "?",
                            fontSize = 36.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome completo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PurpleDarker)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = { dataNascimento = it },
                    label = { Text("Data de Nascimento (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fotoPerfil,
                    onValueChange = { fotoPerfil = it },
                    label = { Text("URL da Foto de Perfil") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tipoUsuario,
                    onValueChange = { tipoUsuario = it },
                    label = { Text("Tipo de Usuário") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val dataFormatada = dataNascimento.take(10)
                        val usuarioAtualizado = usuario.copy(
                            nome_completo = nome,
                            email = email,
                            data_nascimento = dataFormatada,
                            foto_perfil = fotoPerfil,
                            descricao = descricao,
                            senha = senha,
                            tipo_usuario = tipoUsuario
                        )

                        RetrofitInstance.usuarioService
                            .atualizarUsuarioPorId(usuario.id_usuario, usuarioAtualizado)
                            .enqueue(object : retrofit2.Callback<Usuario> {
                                override fun onResponse(
                                    call: retrofit2.Call<Usuario>,
                                    response: retrofit2.Response<Usuario>
                                ) {
                                    if (response.isSuccessful) {
                                        onSave(usuarioAtualizado)
                                        navController.popBackStack()
                                    } else {
                                        Log.e("EditarInfo", "Erro ao atualizar usuário: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: retrofit2.Call<Usuario>, t: Throwable) {
                                    Log.e("EditarInfo", "Falha na atualização: ${t.message}")
                                }
                            })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter)
                ) {
                    Text("Salvar alterações", color = Color(0xFF341E9B))
                }
            }
        }
    }
}



@Composable
fun EditarInfoWrapper(navController: NavController, idUsuario: Int?) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(idUsuario) {
        if (idUsuario != null) {
            try {
                val result = RetrofitInstance.usuarioService.getUsuarioPorIdSuspend(idUsuario)
                usuario = result.usuario?.firstOrNull()
                if (usuario == null) errorMessage = "Usuário não encontrado"
            } catch (e: Exception) {
                Log.e("EditarInfo", "Erro ao carregar usuário: ${e.message}")
                errorMessage = "Erro ao carregar usuário"
            } finally {
                loading = false
            }
        } else {
            loading = false
            errorMessage = "ID do usuário inválido"
        }
    }

    when {
        loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        usuario != null -> {
            EditarInfo(
                navController = navController,
                usuario = usuario!!,
                onSave = { usuarioAtualizado ->
                    // Atualize SharedPreferences ou estado global se necessário
                    navController.popBackStack()
                }
            )
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage ?: "Erro desconhecido")
            }
        }
    }
}
