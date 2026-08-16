package com.monvicsar.tecnico.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monvicsar.tecnico.data.Ticket
import com.monvicsar.tecnico.data.TicketStatus
import com.monvicsar.tecnico.ui.components.StatusPill
import com.monvicsar.tecnico.ui.theme.PriorityUrgent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(ticket: Ticket, onBack: () -> Unit, modifier: Modifier = Modifier) {
    var status by remember { mutableStateOf(ticket.status) }
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(ticket.id, style = MaterialTheme.typography.labelMedium)
                        Text(ticket.type, style = MaterialTheme.typography.titleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Prioridad", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "● ${ticket.priority.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            color = PriorityUrgent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    androidx.compose.foundation.layout.Box {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickableChip { menuOpen = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(
                                    status.label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            TicketStatus.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        status = option
                                        menuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                InfoBlock(label = "Cliente") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ticket.client, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "${ticket.contactName} · ${ticket.contactPhone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FloatingActionButton(
                            onClick = { /* TODO: Intent.ACTION_DIAL en fase de integracion real */ },
                            containerColor = androidx.compose.ui.graphics.Color(0xFF2E8F67),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Llamar", tint = Color.White)
                        }
                    }
                }
            }

            item {
                InfoBlock(label = "Equipo ATM") {
                    FieldGrid(
                        listOf(
                            "Modelo" to ticket.equipmentModel,
                            "Serie" to ticket.equipmentSerial,
                            "Ubicación" to ticket.equipmentLocation,
                            "Instalado" to ticket.equipmentInstalled
                        )
                    )
                }
            }

            item {
                InfoBlock(label = "Resumen del problema") {
                    Text(ticket.problemSummary, style = MaterialTheme.typography.bodyMedium)
                }
            }

            item {
                InfoBlock(label = "Fechas") {
                    FieldGrid(
                        listOf(
                            "Creado" to ticket.createdLabel,
                            "Vence (SLA)" to ticket.dueSlaLabel
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoBlock(label: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun FieldGrid(fields: List<Pair<String, String>>) {
    val rows = fields.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { (label, value) ->
                    Column {
                        Text(
                            label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun Modifier.clickableChip(onClick: () -> Unit): Modifier =
    this.then(Modifier.clickable(onClick = onClick))
