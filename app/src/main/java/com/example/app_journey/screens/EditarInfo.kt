package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Usuario
import com.example.app_journey.service.RetrofitInstance
import com.example.app_journey.ui.theme.*
import com.example.app_journey.utils.AzureUploader
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf(usuario.nome_completo) }
    var email by remember { mutableStateOf(usuario.email) }
    var dataNascimento by remember { mutableStateOf(usuario.data_nascimento?.take(10) ?: "") }
    var descricao by remember { mutableStateOf(usuario.descricao ?: "") }
    var senha by remember { mutableStateOf("") } // não mostra senha criptografada
    var tipoUsuario by remember { mutableStateOf(usuario.tipo_usuario) }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemUrl by remember { mutableStateOf(usuario.foto_perfil ?: "") }
    var enviando by remember { mutableStateOf(false) }

    // Launcher para pegar imagem da galeria
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagemUri = uri
        uri?.let {
            scope.launch {
                enviando = true
                val inputStream = context.contentResolver.openInputStream(it)
                val fileName = "foto_perfil_${System.currentTimeMillis()}.jpg"
                if (inputStream != null) {
                    val url = AzureUploader.uploadImageToAzure(inputStream, fileName)
                    if (url != null) {
                        imagemUrl = url
                        Toast.makeText(context, "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Falha no upload da imagem", Toast.LENGTH_SHORT).show()
                    }
                }
                enviando = false
            }
        }
    }

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

        Spacer(modifier = Modifier.height(12.dp))

        // Card com imagem e nome
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
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imagemUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(imagemUrl),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Toque para alterar a foto", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome completo") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PurpleDarker)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = {
                        // mantém formato AAAA-MM-DD
                        dataNascimento = it.take(10).replace(Regex("[^0-9-]"), "")
                    },
                    label = { Text("Data de Nascimento (AAAA-MM-DD)") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Nova Senha") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tipoUsuario,
                    onValueChange = { tipoUsuario = it },
                    label = { Text("Tipo de Usuário") },
                    shape = RoundedCornerShape(33.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val usuarioAtualizado = usuario.copy(
                            nome_completo = nome,
                            email = email,
                            data_nascimento = dataNascimento.take(10),
                            foto_perfil = imagemUrl,
                            descricao = descricao,
                            senha = if (senha.isNotBlank()) senha else usuario.senha,
                            tipo_usuario = tipoUsuario
                        )

                        RetrofitInstance.usuarioService
                            .atualizarUsuarioPorId(usuario.id_usuario, usuarioAtualizado)
                            .enqueue(object : Callback<Usuario> {
                                override fun onResponse(
                                    call: Call<Usuario>,
                                    response: Response<Usuario>
                                ) {
                                    if (response.isSuccessful) {
                                        onSave(usuarioAtualizado)
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("usuarioAtualizado", usuarioAtualizado)
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Erro ao atualizar (${response.code()})", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                    Toast.makeText(context, "Falha: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleLighter)
                ) {
                    Text("Salvar alterações", color = Color(0xFF341E9B))
                }
            }
        }
    }

    if (enviando) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
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
                errorMessage = "Erro ao carregar usuário"
            } finally {
                loading = false
            }
        } else {
            loading = false
            errorMessage = "ID inválido"
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
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("usuarioAtualizado", usuarioAtualizado)
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

@Preview(showBackground = true)
@Composable
private fun PreviewEditarInfo() {
    val fakeNav = rememberNavController()

    val fakeUsuario = Usuario(
        id_usuario = 1,
        nome_completo = "Nicolas Lima",
        email = "nicolas@email.com",
        senha = "123456",
        data_nascimento = "2000-05-20",
        descricao = "Explorador do mundo e amante de tecnologia.",
        tipo_usuario = "Comum",
        foto_perfil = "https://i.pravatar.cc/300" // imagem de teste
    )

    EditarInfo(
        navController = fakeNav,
        usuario = fakeUsuario,
        onSave = {}
    )
}
