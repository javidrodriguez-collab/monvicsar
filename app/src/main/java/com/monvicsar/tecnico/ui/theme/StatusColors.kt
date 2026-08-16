package com.monvicsar.tecnico.ui.theme

import androidx.compose.ui.graphics.Color

// Tipo de Llamada / prioridad (franja lateral): Tipo 1 = Urgente/Critico (rojo),
// Tipo 2 = Alta (naranja), Tipo 3 = Operativo/No urgente (amarillo), Tipo 4 (azul).
val PriorityUrgent = Color(0xFFD3402F)
val PriorityHigh = Color(0xFFC6821F)
val PriorityNormal = Color(0xFFE0B400)
val PriorityType4 = Color(0xFF2D6CDF)

// Estado (chip) — fondo suave + texto
val StatusAssignBg = Color(0xFFEEF1F6)
val StatusAssignFg = Color(0xFF5B6B84)

val StatusTravelingBg = Color(0xFFF8E9D2)
val StatusTravelingFg = Color(0xFFC6821F)

// "Working" usa el cobre del logo (tronco del arbol) en vez de un tono verde,
// para no confundirse visualmente con el verde de marca ni con "Closed".
val StatusWorkingBg = Color(0xFFFBE4D8)
val StatusWorkingFg = Color(0xFFB25C3D)

val StatusSuspendFg = Color(0xFF5B6B84)

val StatusClosedBg = Color(0xFFDCEFE6)
val StatusClosedFg = Color(0xFF2E8F67)

// Verde/turqueza de marca Monvicsar, extraido del logo real (logo tecnico verde.png).
val BrandGreen = Color(0xFF0F7A73)
