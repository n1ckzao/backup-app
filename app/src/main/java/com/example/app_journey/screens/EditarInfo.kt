package com.example.app_journey.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarInfo(
    navController: NavController,
    usuario: Usuario,
    onSave: (Usuario) -> Unit
) {
    // Estados dos campos do formulário
    var nome by remember { mutableStateOf(usuario.nome_completo) }
    var email by remember { mutableStateOf(usuario.email) }
    var dataNascimento by remember { mutableStateOf(usuario.data_nascimento ?: "") }
    var fotoPerfil by remember { mutableStateOf(usuario.foto_perfil ?: "") }
    var descricao by remember { mutableStateOf(usuario.descricao ?: "") }
    var senha by remember { mutableStateOf(usuario.senha) }
    var tipoUsuario by remember { mutableStateOf(usuario.tipo_usuario) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Perfil", fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // Nome
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Data de nascimento
        OutlinedTextField(
            value = dataNascimento,
            onValueChange = { dataNascimento = it },
            label = { Text("Data de Nascimento (AAAA-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Foto de perfil
        OutlinedTextField(
            value = fotoPerfil,
            onValueChange = { fotoPerfil = it },
            label = { Text("URL da Foto de Perfil") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Descrição
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Senha
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tipo de usuário
        OutlinedTextField(
            value = tipoUsuario,
            onValueChange = { tipoUsuario = it },
            label = { Text("Tipo de Usuário") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Botão salvar
        Button(
            onClick = {
                val dataFormatada = dataNascimento.take(10) // "2007-12-12"

                val usuarioAtualizado = usuario.copy(
                    nome_completo = nome,
                    email = email,
                    data_nascimento = dataFormatada,
                    foto_perfil = fotoPerfil,
                    descricao = descricao,
                    senha = senha,
                    tipo_usuario = tipoUsuario
                )

                // Chamada PUT para atualizar usuário
                RetrofitInstance.usuarioService
                    .atualizarUsuarioPorId(usuario.id_usuario, usuarioAtualizado)
                    .enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful) {
                                Log.d("EditarInfo", "Usuário atualizado com sucesso!")
                                onSave(usuarioAtualizado)
                                navController.popBackStack()
                            } else {
                                Log.e("EditarInfo", "Erro ao atualizar usuário: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e("EditarInfo", "Falha na atualização: ${t.message}")
                        }
                    })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar alterações")
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
