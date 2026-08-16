package com.monvicsar.tecnico.data

import androidx.compose.runtime.mutableStateMapOf

/**
 * Cambios guardados localmente por el tecnico que todavia no se han enviado al
 * servidor de ERPNext. Se acumulan por ticket hasta que el tecnico presiona
 * "Sincronizar" en la pantalla principal.
 */
class TicketSyncState {
    val pendingChanges = mutableStateMapOf<String, TicketStatus>()

    fun statusFor(ticket: Ticket): TicketStatus = pendingChanges[ticket.id] ?: ticket.status

    fun saveStatus(ticketId: String, newStatus: TicketStatus) {
        pendingChanges[ticketId] = newStatus
    }

    fun syncAll() {
        // TODO: enviar cada cambio pendiente a la API de ERPNext (PUT HD Ticket status)
        pendingChanges.clear()
    }
}
