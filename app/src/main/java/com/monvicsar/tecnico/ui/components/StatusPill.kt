package com.monvicsar.tecnico.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monvicsar.tecnico.data.TicketPriority
import com.monvicsar.tecnico.data.TicketStatus
import com.monvicsar.tecnico.ui.theme.PriorityHigh
import com.monvicsar.tecnico.ui.theme.PriorityNormal
import com.monvicsar.tecnico.ui.theme.PriorityType4
import com.monvicsar.tecnico.ui.theme.PriorityUrgent
import com.monvicsar.tecnico.ui.theme.StatusAssignBg
import com.monvicsar.tecnico.ui.theme.StatusAssignFg
import com.monvicsar.tecnico.ui.theme.StatusClosedBg
import com.monvicsar.tecnico.ui.theme.StatusClosedFg
import com.monvicsar.tecnico.ui.theme.StatusSuspendFg
import com.monvicsar.tecnico.ui.theme.StatusTravelingBg
import com.monvicsar.tecnico.ui.theme.StatusTravelingFg
import com.monvicsar.tecnico.ui.theme.StatusWorkingBg
import com.monvicsar.tecnico.ui.theme.StatusWorkingFg

@Composable
fun StatusPill(status: TicketStatus, modifier: Modifier = Modifier) {
    when (status) {
        TicketStatus.SUSPEND -> Surface(
            modifier = modifier,
            shape = RoundedCornerShape(99.dp),
            color = androidx.compose.ui.graphics.Color.Transparent,
            border = BorderStroke(1.5.dp, StatusSuspendFg)
        ) {
            Text(
                text = status.label,
                color = StatusSuspendFg,
                fontSize = 11.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        else -> {
            val (bg, fg) = when (status) {
                TicketStatus.ASSIGN -> StatusAssignBg to StatusAssignFg
                TicketStatus.TRAVELING -> StatusTravelingBg to StatusTravelingFg
                TicketStatus.WORKING -> StatusWorkingBg to StatusWorkingFg
                TicketStatus.CLOSED -> StatusClosedBg to StatusClosedFg
                TicketStatus.SUSPEND -> StatusAssignBg to StatusAssignFg // unreachable
            }
            Surface(modifier = modifier, shape = RoundedCornerShape(99.dp), color = bg) {
                Text(
                    text = status.label,
                    color = fg,
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun priorityColor(priority: TicketPriority) = when (priority) {
    TicketPriority.URGENT -> PriorityUrgent
    TicketPriority.HIGH -> PriorityHigh
    TicketPriority.NORMAL -> PriorityNormal
    TicketPriority.TYPE4 -> PriorityType4
}
