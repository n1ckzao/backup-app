package com.example.app_journey.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Usuario
import com.example.app_journey.model.UsuarioResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.ui.theme.*
import com.example.app_journey.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditarInfo(navegacao: NavHostController) {
    val usuarioLogado = remember { mutableStateOf<Usuario?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    LaunchedEffect(idUsuario) {
        if (idUsuario != -1) {
            loading.value = true
            val usuarioService = RetrofitFactory().getUsuarioService()
            usuarioService.getUsuarioPorId(idUsuario)
                .enqueue(object : Callback<UsuarioResult> {
                    override fun onResponse(
                        call: Call<UsuarioResult>,
                        response: Response<UsuarioResult>
                    ) {
                        loading.value = false
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null && result.usuario != null && result.usuario.isNotEmpty()) {
                                usuarioLogado.value = result.usuario[0] // pega o primeiro usuário
                                errorMessage.value = null
                            } else {
                                errorMessage.value = "Usuário não encontrado"
                            }
                        } else {
                            errorMessage.value = "Erro ao carregar usuário: ${response.code()}"
                        }
                    }

                    override fun onFailure(call: Call<UsuarioResult>, t: Throwable) {
                        loading.value = false
                        errorMessage.value = "Erro de rede: ${t.message}"
                    }
                })
        } else {
            errorMessage.value = "Usuário não logado"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(16.dp)
    ) {
        when {
            loading.value -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            errorMessage.value != null -> Text(
                text = errorMessage.value ?: "Erro desconhecido",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
            usuarioLogado.value != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        profileImageUri = usuarioLogado.value!!.foto_perfil?.let { Uri.parse(it) },
                        nome = usuarioLogado.value!!.nome_completo,
                        email = usuarioLogado.value!!.email,
                        onSelectImage = {},
                        onEditClick = { navegacao.navigate("editar_info") }
                    )
                    CardBio(
                        descricao = usuarioLogado.value!!.descricao ?: "",
                        onEditClick = { navegacao.navigate("editar_info") }
                    )
                }
            }
        }
    }
}

@Composable
fun CardInfoPessoais(
    profileImageUri: Uri?,
    nome: String?,
    email: String?,
    onSelectImage: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PurpleDarker)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Informações pessoais",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter)
                ) {
                    Text("Editar", color = Color(0xFF341E9B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        tint = White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Button(
                    onClick = onSelectImage,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter)
                ) {
                    Text("Enviar foto", color = Color(0xFF341E9B))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoRow("Nome completo", nome)
            InfoRow("Email", email)
        }
    }
}

@Composable
fun CardBio(onEditClick: () -> Unit, descricao: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PurpleDarker)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Biografia",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter)
                ) {
                    Text("Editar", color = Color(0xFF341E9B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = descricao.ifBlank { "Nenhuma biografia cadastrada" },
                color = White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", color = White, fontWeight = FontWeight.Medium)
        if (value != null) {
            Text(text = value, color = White)
        }
    }
}

@Preview
@Composable
private fun PerfilPreview() {
    val fakeNavController = rememberNavController()
    Perfil(navegacao = fakeNavController)
}