package com.example.app_journey.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_journey.R

@Composable
fun TelaInicial(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF341E9B)) // Roxo de fundo
    ) {
        // Fundo ilustrativo
        Image(
            painter = painterResource(id = R.drawable.splash_background), // substitua se quiser
            contentDescription = "Background Journey",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(0.55f).offset(y = (-80).dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Começa sua\naventura com\nJOURNEY!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent.copy(alpha = 0.2f))
                    .clickable { navController.navigate("login") },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { navController.navigate("login") },
                    contentAlignment = Alignment.Center
                ) {

                    val scale = remember { mutableStateOf(true) }
                    val animatedScale by animateFloatAsState(
                        targetValue = if (scale.value) 1.45f else 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(700),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    LaunchedEffect (Unit) { scale.value = !scale.value }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Avançar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(34.dp)
                            .scale(animatedScale) // <<< pulsando :)
                    )

                }
            }
        }
    }
}
