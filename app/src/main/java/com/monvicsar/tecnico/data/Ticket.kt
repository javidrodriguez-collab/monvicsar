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

// Tipo de Llamada: Tipo 1 = URGENT, Tipo 2 = HIGH, Tipo 3 = NORMAL,
// Tipo 4 = TYPE4 (pausa el reloj de SLA, requiere comentario de justificacion -- ver Fase futura).
enum class TicketPriority(val label: String) {
    URGENT("Urgente"),
    HIGH("Alta"),
    NORMAL("Operativo"),
    TYPE4("Tipo 4")
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
        ),
        Ticket(
            id = "WO #2026-0844",
            type = "Soporte Técnico",
            client = "Asociación Popular de Ahorros y Préstamos",
            site = "Suc. Bella Vista",
            status = TicketStatus.ASSIGN,
            priority = TicketPriority.URGENT,
            dueLabel = "vence 4:30pm",
            contactName = "Sra. Carolina Reyes",
            contactPhone = "809-555-0456",
            equipmentModel = "NCR SelfServ 84",
            equipmentSerial = "NC-84-30512",
            equipmentLocation = "Bella Vista, Sto. Dgo.",
            equipmentInstalled = "2020",
            problemSummary = "Lector de tarjetas retiene tarjetas del cliente de forma intermitente.",
            createdLabel = "16 ago 10:05am",
            dueSlaLabel = "16 ago 4:30pm"
        ),
        Ticket(
            id = "WO #2026-0843",
            type = "Mant. Preventivo",
            client = "Banco BHD",
            site = "Suc. Churchill",
            status = TicketStatus.ASSIGN,
            priority = TicketPriority.TYPE4,
            dueLabel = "vence pasado mañana",
            contactName = "Ing. Pedro Almonte",
            contactPhone = "809-555-0512",
            equipmentModel = "Diebold Opteva 750",
            equipmentSerial = "DB-750-88231",
            equipmentLocation = "Churchill, Sto. Dgo.",
            equipmentInstalled = "2022",
            problemSummary = "Mantenimiento preventivo semestral programado, sin urgencia operativa.",
            createdLabel = "16 ago 7:50am",
            dueSlaLabel = "18 ago 5:00pm"
        ),
        Ticket(
            id = "WO #2026-0842",
            type = "Soporte Técnico",
            client = "Scotiabank",
            site = "Suc. Winston Churchill",
            status = TicketStatus.SUSPEND,
            priority = TicketPriority.HIGH,
            dueLabel = "esp. repuesto",
            contactName = "Ing. Daniel Vargas",
            contactPhone = "809-555-0389",
            equipmentModel = "NCR SelfServ 22e",
            equipmentSerial = "NC-22E-11290",
            equipmentLocation = "Winston Churchill, Sto. Dgo.",
            equipmentInstalled = "2019",
            problemSummary = "Tarjeta controladora dañada, en espera de pieza desde almacén central.",
            createdLabel = "15 ago 2:20pm",
            dueSlaLabel = "pausado"
        ),
        Ticket(
            id = "WO #2026-0841",
            type = "Field Service",
            client = "Banco Popular Dominicano",
            site = "Suc. Los Jardines",
            status = TicketStatus.ASSIGN,
            priority = TicketPriority.NORMAL,
            dueLabel = "vence mañana",
            contactName = "Sra. Yolanda Féliz",
            contactPhone = "809-555-0271",
            equipmentModel = "NCR SelfServ 84",
            equipmentSerial = "NC-84-19875",
            equipmentLocation = "Los Jardines, Sto. Dgo.",
            equipmentInstalled = "2018",
            problemSummary = "Impresora de recibos no imprime, cliente reporta papel atascado.",
            createdLabel = "15 ago 11:40am",
            dueSlaLabel = "17 ago 12:00pm"
        ),
        Ticket(
            id = "WO #2026-0840",
            type = "Soporte Técnico",
            client = "Banreservas",
            site = "Suc. Av. Duarte",
            status = TicketStatus.ASSIGN,
            priority = TicketPriority.URGENT,
            dueLabel = "vence 5:00pm",
            contactName = "Ing. Ramón Castillo",
            contactPhone = "809-555-0630",
            equipmentModel = "Diebold Opteva 590",
            equipmentSerial = "DB-590-65120",
            equipmentLocation = "Av. Duarte, Sto. Dgo.",
            equipmentInstalled = "2020",
            problemSummary = "ATM completamente fuera de servicio, no enciende la pantalla principal.",
            createdLabel = "16 ago 6:55am",
            dueSlaLabel = "16 ago 5:00pm"
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
