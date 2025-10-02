package com.example.app_journey.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app_journey.ui.theme.JouneyperfilTheme
import com.example.app_journey.ui.theme.White


import com.example.app_journey.ui.theme.Red
import com.example.app_journey.ui.theme.PrimaryPurple

@Composable
fun DrawerMenu(
    onOptionSelected: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = PrimaryPurple.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp)
        ) {
            // Itens do menu
            DrawerItem(
                text = "Criar Grupo",
                icon = Icons.Default.Add,
                onClick = { onOptionSelected("criar_grupo") }
            )
            DrawerItem(
                text = "Meus Grupos",
                icon = Icons.Default.Person,
                onClick = { onOptionSelected("Meus Grupos") }
            )
            DrawerItem(
                text = "Chats",
                icon = Icons.Default.Call,
                onClick = { onOptionSelected("Chats") }
            )
            DrawerItem(
                text = "E-Books",
                icon = Icons.Default.Info,
                onClick = { onOptionSelected("E-Books") }
            )

            Spacer(modifier = Modifier.weight(1f))


            DrawerItem(
                text = "Apagar Perfil",
                icon = Icons.Default.Delete,
                iconTint = Red,
                textColor = Red,
                onClick = { onOptionSelected("Apagar Perfil") }
            )
        }
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    iconTint: Color = White,
    textColor: Color = White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp
        )
    }
}
@Preview(showBackground = true)
@Composable
fun DrawerMenuPreview() {
    JouneyperfilTheme {
        DrawerMenu(onOptionSelected = {})
    }
}