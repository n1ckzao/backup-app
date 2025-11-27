package com.example.app_journey.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
fun Perfil(navController: NavHostController) {
    val usuarioLogado = remember { mutableStateOf<Usuario?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val idUsuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1

    LaunchedEffect(idUsuario) {
        if (idUsuario != -1) {
            loading.value = true
            RetrofitFactory().getUsuarioService().getUsuarioPorId(idUsuario)
                .enqueue(object : Callback<UsuarioResult> {
                    override fun onResponse(
                        call: Call<UsuarioResult>,
                        response: Response<UsuarioResult>
                    ) {
                        loading.value = false
                        if (response.isSuccessful) {
                            val result = response.body()
                            usuarioLogado.value = result?.usuario?.firstOrNull()
                        } else {
                            errorMessage.value = "Erro ao carregar usuário."
                        }
                    }
                    override fun onFailure(call: Call<UsuarioResult>, t: Throwable) {
                        loading.value = false
                        errorMessage.value = "Erro de rede: ${t.message}"
                    }
                })
        } else errorMessage.value = "Usuário não logado"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryPurple)
            .padding(20.dp)
    ) {
        when {
            loading.value -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            errorMessage.value != null -> Text(
                text = errorMessage.value ?: "Erro",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )

            usuarioLogado.value != null -> PerfilContent(
                usuario = usuarioLogado.value!!,
                idUsuario = idUsuario,
                navController = navController
            )
        }
    }
}

@Composable
fun PerfilContent(usuario: Usuario, idUsuario: Int, navController: NavHostController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(Modifier.height(16.dp))

        // FOTO DO PERFIL
        Box(contentAlignment = Alignment.Center) {
            if (!usuario.foto_perfil.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(usuario.foto_perfil),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto padrão",
                    tint = Color.White,
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // NOME
        Text(
            text = usuario.nome_completo ?: "Usuário",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        // EMAIL
        Text(
            text = usuario.email ?: "",
            color = Color.White.copy(0.8f),
            fontSize = 16.sp
        )

        Spacer(Modifier.height(30.dp))

        // CARD DE INFORMAÇÕES
        CardInformacoes(usuario, onEditClick = {
            navController.navigate("editar_info/${idUsuario}")
        })

        Spacer(Modifier.height(20.dp))

        // CARD BIO
        CardBiografia(usuario.descricao ?: "", onEditClick = {
            navController.navigate("editar_info/${idUsuario}")
        })
    }
}

@Composable
fun CardInformacoes(usuario: Usuario, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PurpleDarker),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Informações pessoais", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PurpleLighter)
                }
            }

            Spacer(Modifier.height(12.dp))

            InfoRow("Nome completo", usuario.nome_completo)
            InfoRow("Email", usuario.email)
        }
    }
}

@Composable
fun CardBiografia(descricao: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PurpleDarker),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Biografia", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PurpleLighter)
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = descricao.ifBlank { "Nenhuma biografia cadastrada." },
                color = White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, color = White.copy(0.7f), fontSize = 13.sp)
        Text(value ?: "", color = White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
    }
}
