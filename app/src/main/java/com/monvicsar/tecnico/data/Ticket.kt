package com.monvicsar.tecnico.data

enum class TicketStatus(val label: String) {
    ASSIGN("Assign"),
    TRAVELING("Traveling"),
    WORKING("Working"),
    SUSPEND("Suspend"),
    CLOSED("Closed");

    /**
     * Ciclo de vida fijo del ticket: Assign -> Traveling -> Working -> (Suspend | Closed),
     * y Suspend -> Working al retomar. "Assign" nunca es un destino valido: solo lo
     * establece el despachador al crear/asignar el ticket, el tecnico no puede volver a el.
     */
    fun validNextStates(): List<TicketStatus> = when (this) {
        ASSIGN -> listOf(TRAVELING)
        TRAVELING -> listOf(WORKING)
        WORKING -> listOf(SUSPEND, CLOSED)
        SUSPEND -> listOf(WORKING)
        CLOSED -> emptyList()
    }

    /** Estados que implican que el tecnico esta fisicamente ocupado con ESTE ticket. */
    fun isActiveFieldState(): Boolean = this == TRAVELING || this == WORKING
}

enum class TicketPriority {
    URGENT, HIGH, NORMAL
}

data class Ticket(
    val id: String,
    val type: String,
    val client: String,
    val site: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    val dueLabel: String,
    val contactName: String,
    val contactPhone: String,
    val equipmentModel: String,
    val equipmentSerial: String,
    val equipmentLocation: String,
    val equipmentInstalled: String,
    val problemSummary: String,
    val createdLabel: String,
    val dueSlaLabel: String
)

object SampleTickets {
    val all = listOf(
        Ticket(
            id = "WO #2026-0847",
            type = "Field Service",
            client = "Banco Popular Dominicano",
            site = "Suc. Piantini · Av. Abraham Lincoln 1069",
            status = TicketStatus.WORKING,
            priority = TicketPriority.URGENT,
            dueLabel = "vence 3:00pm",
            contactName = "Ing. Rafael Cruz",
            contactPhone = "809-555-0142",
            equipmentModel = "NCR SelfServ 84",
            equipmentSerial = "NC-84-22913",
            equipmentLocation = "Piantini, Sto. Dgo.",
            equipmentInstalled = "2019",
            problemSummary = "Dispensador no entrega efectivo, pantalla muestra error E-108. " +
                "Cliente reporta 3 intentos fallidos esta mañana.",
            createdLabel = "16 ago 9:12am",
            dueSlaLabel = "16 ago 3:00pm"
        ),
        Ticket(
            id = "WO #2026-0848",
            type = "Soporte Técnico",
            client = "Banreservas",
            site = "Suc. Naco",
            status = TicketStatus.TRAVELING,
            priority = TicketPriority.HIGH,
            dueLabel = "vence 11:30am",
            contactName = "Sra. Melissa Peña",
            contactPhone = "809-555-0198",
            equipmentModel = "Diebold Opteva 590",
            equipmentSerial = "DB-590-77410",
            equipmentLocation = "Naco, Sto. Dgo.",
            equipmentInstalled = "2021",
            problemSummary = "Cliente reporta pantalla congelada, ATM fuera de servicio desde anoche.",
            createdLabel = "16 ago 8:40am",
            dueSlaLabel = "16 ago 11:30am"
        ),
        Ticket(
            id = "WO #2026-0846",
            type = "Mant. Preventivo",
            client = "Banreservas",
            site = "Suc. 27 de Febrero",
            status = TicketStatus.ASSIGN,
            priority = TicketPriority.NORMAL,
            dueLabel = "vence mañana",
            contactName = "Sr. Manuel Objío",
            contactPhone = "809-555-0223",
            equipmentModel = "NCR SelfServ 22e",
            equipmentSerial = "NC-22E-10847",
            equipmentLocation = "27 de Febrero, Sto. Dgo.",
            equipmentInstalled = "2018",
            problemSummary = "Mantenimiento preventivo trimestral programado.",
            createdLabel = "15 ago 4:00pm",
            dueSlaLabel = "17 ago 5:00pm"
        ),
        Ticket(
            id = "WO #2026-0845",
            type = "Soporte Técnico",
            client = "IAD Caribe ATM Solutions",
            site = "Plaza Central, La Romana",
            status = TicketStatus.SUSPEND,
            priority = TicketPriority.NORMAL,
            dueLabel = "esp. repuesto",
            contactName = "Ing. Luis Fermín",
            contactPhone = "809-555-0311",
            equipmentModel = "NCR SelfServ 22e",
            equipmentSerial = "NC-22E-10203",
            equipmentLocation = "Plaza Central, La Romana",
            equipmentInstalled = "2017",
            problemSummary = "Módulo dispensador dañado, esperando repuesto del laboratorio.",
            createdLabel = "14 ago 10:15am",
            dueSlaLabel = "pausado"
        )
    )

    val closed = listOf(
        Ticket(
            id = "WO #2026-0839",
            type = "Lab. REWORK",
            client = "Banco Popular Dominicano",
            site = "Laboratorio Monvicsar",
            status = TicketStatus.CLOSED,
            priority = TicketPriority.NORMAL,
            dueLabel = "cerrado",
            contactName = "Ing. Rafael Cruz",
            contactPhone = "809-555-0142",
            equipmentModel = "NCR SelfServ 84 — módulo dispensador",
            equipmentSerial = "NC-84-19204",
            equipmentLocation = "Laboratorio Monvicsar",
            equipmentInstalled = "2019",
            problemSummary = "Módulo dispensador reparado y devuelto a inventario de intercambio.",
            createdLabel = "10 ago 9:00am",
            dueSlaLabel = "cerrado 12 ago"
        )
    )
}
