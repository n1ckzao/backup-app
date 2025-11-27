package com.example.app_journey.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app_journey.model.Grupo
import com.example.app_journey.model.GruposResult
import com.example.app_journey.service.RetrofitFactory
import com.example.app_journey.utils.AzureUploader
import com.example.app_journey.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CriarGrupo(navegacao: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var id_area by remember { mutableStateOf<Int?>(null) }
    var limite by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var imagemUri by remember { mutableStateOf<Uri?>(null) }
    var imagemUrl by remember { mutableStateOf<String?>(null) }
    val id_usuario = SharedPrefHelper.recuperarIdUsuario(context) ?: -1
    var mensagem by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }

    val areas = remember { mutableStateListOf<com.example.app_journey.model.Area>() }
    var areaSelecionada by remember { mutableStateOf<com.example.app_journey.model.Area?>(null) }
    var expandedArea by remember { mutableStateOf(false) }

    // Carregar áreas
    LaunchedEffect(Unit) {
        RetrofitFactory().getAreaService().listarAreas()
            .enqueue(object : Callback<com.example.app_journey.model.AreaResult> {
                override fun onResponse(call: Call<com.example.app_journey.model.AreaResult>, response: Response<com.example.app_journey.model.AreaResult>) {
                    if (response.isSuccessful) {
                        response.body()?.areas?.let {
                            areas.clear()
                            areas.addAll(it)
                        }
                    }
                }
                override fun onFailure(call: Call<com.example.app_journey.model.AreaResult>, t: Throwable) {
                    Toast.makeText(context, "Erro ao carregar áreas", Toast.LENGTH_SHORT).show()
                }
            })
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagemUri = uri
        uri?.let {
            scope.launch {
                enviando = true
                val inputStream = context.contentResolver.openInputStream(it)
                val fileName = "imagem_${System.currentTimeMillis()}.jpg"
                if (inputStream != null) {
                    val url = AzureUploader.uploadImageToAzure(inputStream, fileName)
                    if (url != null) imagemUrl = url
                    else Toast.makeText(context, "Falha no upload da imagem", Toast.LENGTH_SHORT).show()
                }
                enviando = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4A33C3), Color(0xFFD9DCFC))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Crie seu Grupo no JOURNEY!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF341E9B)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Botão Voltar
                    OutlinedButton(
                        onClick = { navegacao.popBackStack() },
                        modifier = Modifier.height(45.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF341E9B))
                        Text(" Voltar", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold)
                    }

                    CampoTexto("Nome do Grupo", nome) { nome = it }

                    // Dropdown Áreas
                    Text("Área Específica", color = Color.White, fontWeight = FontWeight.Bold)
                    Box {
                        Button(
                            onClick = { expandedArea = !expandedArea },
                            modifier = Modifier.fillMaxWidth().height(55.dp),
                            shape = RoundedCornerShape(33.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A33C3))
                        ) {
                            Text(areaSelecionada?.area ?: "Selecione a área", color = Color.White)
                        }
                        DropdownMenu(expanded = expandedArea, onDismissRequest = { expandedArea = false }) {
                            areas.forEach { area ->
                                DropdownMenuItem(text = { Text(area.area) }, onClick = {
                                    areaSelecionada = area
                                    id_area = area.id_area
                                    expandedArea = false
                                })
                            }
                        }
                    }

                    CampoTexto("Limite de Membros", limite.filter { it.isDigit() }) { limite = it }
                    CampoTexto("Descrição", descricao) { descricao = it }

                    Text("Imagem do Grupo", color = Color.White, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF4A33C3))
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagemUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imagemUrl),
                                contentDescription = "Imagem selecionada",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("📁 Selecionar Imagem", color = Color.White, fontStyle = FontStyle.Italic)
                        }
                    }

                    Button(
                        onClick = {
                            if (nome.isBlank() || id_area == null || limite.isBlank() || descricao.isBlank() || imagemUrl == null) {
                                mensagem = "Preencha todos os campos e envie uma imagem"
                                return@Button
                            }

                            val novoGrupo = Grupo(
                                id_grupo = 0,
                                nome = nome,
                                limite_membros = limite.toInt(),
                                descricao = descricao,
                                imagem = imagemUrl!!,
                                id_area = id_area!!,
                                id_usuario = id_usuario
                            )

                            RetrofitFactory().getGrupoService().inserirGrupo(novoGrupo)
                                .enqueue(object : Callback<GruposResult> {
                                    override fun onResponse(call: Call<GruposResult>, response: Response<GruposResult>) {
                                        if (response.isSuccessful && response.body()?.status == true) {
                                            Toast.makeText(context, "Grupo criado com sucesso!", Toast.LENGTH_SHORT).show()
                                            navegacao.navigate("home")
                                        } else {
                                            mensagem = "Erro ao criar grupo: ${response.code()}"
                                        }
                                    }

                                    override fun onFailure(call: Call<GruposResult>, t: Throwable) {
                                        mensagem = "Erro de rede: ${t.message}"
                                    }
                                })
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDEBFF))
                    ) {
                        Text("➕ Criar Grupo", color = Color(0xFF341E9B), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    if (mensagem.isNotEmpty()) {
                        Text(mensagem, color = Color.Red)
                    }
                }
            }
        }

        if (enviando) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0x80000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun CampoTexto(label: String, valor: String, aoMudar: (String) -> Unit) {
    Column {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(
            value = valor,
            onValueChange = aoMudar,
            shape = RoundedCornerShape(33.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewCriarGrupoBonito() {
    val fakeNav = rememberNavController()
    CriarGrupo(fakeNav)
}
