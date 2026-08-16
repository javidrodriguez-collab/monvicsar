package com.monvicsar.tecnico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monvicsar.tecnico.ui.theme.MonvicsarTecnicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonvicsarTecnicoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TicketListPlaceholder(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TicketListPlaceholder(modifier: Modifier = Modifier) {
    Text(
        text = "Monvicsar Técnico — lista de tickets próximamente",
        modifier = modifier.padding(16.dp)
    )
}
