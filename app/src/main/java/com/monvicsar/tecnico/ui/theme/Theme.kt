package com.monvicsar.tecnico.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = BlueGrey80,
    tertiary = Teal80
)

// Fondo de pagina (--paper) claramente distinto de la superficie de las tarjetas
// (--surface, blanco) -- sin esto Material3 usa un fondo casi identico al de las
// tarjetas y se pierden los bordes/franjas de color.
private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = Teal40,
    background = Color(0xFFE6E9EE),
    onBackground = Color(0xFF121826),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121826),
    surfaceVariant = Color(0xFFE7EBF2),
    onSurfaceVariant = Color(0xFF57677E),
    outline = Color(0xFFD8DEE8)
)

@Composable
fun MonvicsarTecnicoTheme(
    // Forzado a modo claro siempre: la app esta pensada para uso en campo bajo
    // sol directo, no debe seguir el modo oscuro del sistema del telefono.
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
