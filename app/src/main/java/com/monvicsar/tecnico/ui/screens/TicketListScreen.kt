package com.monvicsar.tecnico.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monvicsar.tecnico.data.SampleTickets
import com.monvicsar.tecnico.data.Ticket
import com.monvicsar.tecnico.data.TicketStatus
import com.monvicsar.tecnico.ui.components.TicketCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketListScreen(
    onOpenDrawer: () -> Unit,
    onTicketClick: (Ticket) -> Unit,
    modifier: Modifier = Modifier
) {
    val openTickets = SampleTickets.all.filter { it.status != TicketStatus.CLOSED }
    val urgentCount = openTickets.count { it.priority == com.monvicsar.tecnico.data.TicketPriority.URGENT }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("Mis Tickets")
                        Text(
                            text = "${openTickets.size} abiertos · $urgentCount urgente${if (urgentCount == 1) "" else "s"}",
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
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
            items(openTickets) { ticket ->
                TicketCard(ticket = ticket, onClick = { onTicketClick(ticket) })
            }
        }
    }
}
