package com.example.app_journey.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(navegacao: NavHostController?) {
    Box(
        modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
    ){
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Text(
                "Bem-vindo ao Journey!",
                fontSize = 37.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                text = "Uma plataforma para mentoria e aprendizado colaborativo.",
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card (
                modifier = Modifier
                    .height(560.dp)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xff351D9B))
            ){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ){
                    Text(
                        "Grupos",
                        fontSize = 34.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.ExtraBold

                    )

                    Spacer(modifier = Modifier.height(13.dp))

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .width(100.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)

                        ) { }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    val fakeNav = rememberNavController()
    Home(navegacao = fakeNav)
}