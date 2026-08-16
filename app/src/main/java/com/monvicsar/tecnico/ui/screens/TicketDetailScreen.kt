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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monvicsar.tecnico.data.Ticket
import com.monvicsar.tecnico.data.TicketStatus
import com.monvicsar.tecnico.ui.components.AppBadge
import com.monvicsar.tecnico.ui.components.StatusPill
import com.monvicsar.tecnico.ui.theme.BrandBlue
import com.monvicsar.tecnico.ui.theme.PriorityUrgent
import androidx.compose.material3.TopAppBarDefaults
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticket: Ticket,
    syncState: com.monvicsar.tecnico.data.TicketSyncState,
    allTickets: List<Ticket>,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var status by remember { mutableStateOf(syncState.statusFor(ticket)) }
    var menuOpen by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun onStatusChanged(newStatus: TicketStatus) {
        if (newStatus !in status.validNextStates()) return
        if (newStatus.isActiveFieldState() && syncState.hasOtherActiveTicket(ticket.id, allTickets)) {
            menuOpen = false
            scope.launch {
                snackbarHostState.showSnackbar("Debes cerrar tu ticket actual antes de viajar a otro")
            }
            return
        }
        status = newStatus
        hasUnsavedChanges = true
        menuOpen = false
    }

    fun onSave() {
        syncState.saveStatus(ticket.id, status)
        hasUnsavedChanges = false
        onSaved()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        AppBadge()
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(ticket.id, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
                            Text(ticket.type, style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: abrir el sitio en Google Maps con la direccion del Equipo ATM */ }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Ver ubicación", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBlue)
            )
        },
        bottomBar = {
            Column {
                if (hasUnsavedChanges) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Cambios sin guardar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                    BottomBarAction(
                        icon = Icons.Default.Description,
                        label = "Notas",
                        modifier = Modifier.weight(1f),
                        onClick = { scope.launch { snackbarHostState.showSnackbar("Disponible en Fase 2") } }
                    )
                    BottomBarAction(
                        icon = Icons.Default.Save,
                        label = "Guardar",
                        modifier = Modifier.weight(1f),
                        enabled = hasUnsavedChanges,
                        highlighted = true,
                        onClick = { onSave() }
                    )
                    BottomBarAction(
                        icon = Icons.Default.Checklist,
                        label = "Cierre",
                        modifier = Modifier.weight(1f),
                        onClick = { scope.launch { snackbarHostState.showSnackbar("Disponible en Fase 2") } }
                    )
                }
            }
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
                    val nextStates = status.validNextStates()
                    androidx.compose.foundation.layout.Box {
                        Surface(
                            shape = RoundedCornerShape(99.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = if (nextStates.isNotEmpty()) {
                                Modifier.clickableChip { menuOpen = true }
                            } else {
                                Modifier
                            }
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
                                if (nextStates.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            nextStates.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = { onStatusChanged(option) }
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

@Composable
private fun BottomBarAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlighted: Boolean = false
) {
    val tint = when {
        highlighted && enabled -> BrandBlue
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f)
    }
    Column(
        modifier = modifier
            .clickable(enabled = true, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}
