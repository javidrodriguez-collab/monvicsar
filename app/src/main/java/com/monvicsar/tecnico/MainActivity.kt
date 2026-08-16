package com.monvicsar.tecnico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder
import com.monvicsar.tecnico.data.SampleTickets
import com.monvicsar.tecnico.data.Ticket
import com.monvicsar.tecnico.data.TicketSyncState
import androidx.compose.runtime.remember
import com.monvicsar.tecnico.ui.screens.ClosedTicketsScreen
import com.monvicsar.tecnico.ui.screens.TicketDetailScreen
import com.monvicsar.tecnico.ui.screens.TicketListScreen
import com.monvicsar.tecnico.ui.theme.MonvicsarTecnicoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonvicsarTecnicoTheme {
                MonvicsarApp()
            }
        }
    }
}

private object Routes {
    const val LIST = "list"
    const val CLOSED = "closed"
    const val DETAIL = "detail/{ticketId}"
    fun detail(ticketId: String) = "detail/${URLEncoder.encode(ticketId, "UTF-8")}"
}

private fun findTicket(id: String): Ticket? =
    (SampleTickets.all + SampleTickets.closed).find { it.id == id }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonvicsarApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val syncState = remember { TicketSyncState() }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)) {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 16.dp, bottom = 12.dp)
                            .size(40.dp)
                    ) {
                        androidx.compose.foundation.layout.Box(
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                "T",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        "Técnico de Campo",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Text(
                        "tecnico@erp.monvicsar.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    NavigationDrawerItem(
                        label = { Text("Mis Tickets") },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        selected = currentRoute == Routes.LIST,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Routes.LIST) {
                                popUpTo(Routes.LIST) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Historial de Tickets Cerrados") },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        selected = currentRoute == Routes.CLOSED,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Routes.CLOSED)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Routes.LIST) {
            composable(Routes.LIST) {
                TicketListScreen(
                    syncState = syncState,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onTicketClick = { ticket -> navController.navigate(Routes.detail(ticket.id)) }
                )
            }
            composable(Routes.CLOSED) {
                ClosedTicketsScreen(
                    onBack = { navController.popBackStack() },
                    onTicketClick = { ticket -> navController.navigate(Routes.detail(ticket.id)) }
                )
            }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
            ) { backStack ->
                val rawTicketId = backStack.arguments?.getString("ticketId").orEmpty()
                val ticketId = URLDecoder.decode(rawTicketId, "UTF-8")
                val ticket = findTicket(ticketId)
                if (ticket != null) {
                    TicketDetailScreen(
                        ticket = ticket,
                        syncState = syncState,
                        allTickets = SampleTickets.all,
                        onBack = { navController.popBackStack() },
                        onSaved = {
                            navController.navigate(Routes.LIST) {
                                popUpTo(Routes.LIST) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
