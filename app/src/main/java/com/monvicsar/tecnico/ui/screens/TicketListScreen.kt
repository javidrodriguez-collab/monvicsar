package com.monvicsar.tecnico.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.monvicsar.tecnico.data.SampleTickets
import com.monvicsar.tecnico.data.Ticket
import com.monvicsar.tecnico.data.TicketStatus
import com.monvicsar.tecnico.data.TicketSyncState
import com.monvicsar.tecnico.ui.components.AppBadge
import com.monvicsar.tecnico.ui.components.TicketCard
import com.monvicsar.tecnico.ui.theme.BrandGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    syncState: TicketSyncState,
    onOpenDrawer: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSyncing by remember { mutableStateOf(false) }

    // Refleja el estado efectivo (incluye cambios guardados localmente aun no sincronizados)
    val displayTickets = SampleTickets.all
        .map { it.copy(status = syncState.statusFor(it)) }
        .filter { it.status != TicketStatus.CLOSED }
    val urgentCount = displayTickets.count { it.priority == com.monvicsar.tecnico.data.TicketPriority.URGENT }
    val pendingCount = syncState.pendingChanges.size

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        AppBadge()
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Mis Tickets", color = Color.White)
                            Text(
                                text = "${displayTickets.size} abiertos · $urgentCount urgente${if (urgentCount == 1) "" else "s"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        enabled = !isSyncing,
                        onClick = {
                            val hadPending = pendingCount > 0
                            if (!hadPending) {
                                scope.launch { snackbarHostState.showSnackbar("No hay cambios pendientes") }
                                return@IconButton
                            }
                            isSyncing = true
                            scope.launch {
                                delay(15_000) // simula el tiempo real de sincronizacion, igual que en GCEW
                                syncState.syncAll()
                                isSyncing = false
                                snackbarHostState.showSnackbar("Sincronizado con el servidor")
                            }
                        }
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            BadgedBox(
                                badge = {
                                    if (pendingCount > 0) {
                                        Badge { Text(pendingCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (pendingCount > 0) Icons.Default.CloudSync else Icons.Default.CloudDone,
                                    contentDescription = "Sincronizar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BrandGreen)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp)
        ) {
            items(displayTickets) { ticket ->
                TicketCard(ticket = ticket, onClick = { onTicketClick(ticket) })
            }
        }
    }
}
