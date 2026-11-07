package com.example.port.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.port.domain.model.PortEntity
import com.example.port.domain.model.PortState

@Composable
fun PortItem(
    port: PortEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (port.state) {
        PortState.OPEN -> colors.tertiaryContainer to colors.onTertiaryContainer
        PortState.CLOSED -> colors.secondaryContainer to colors.onSecondaryContainer
        PortState.FILTERED -> colors.primaryContainer to colors.onPrimaryContainer
        PortState.UNREACHABLE -> colors.errorContainer to colors.onErrorContainer
    }

    val surfaceModifier = modifier
    val body: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Port ${port.portNumber}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = port.state.name,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = port.portName,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    if (onClick != null) {
        Surface(onClick = onClick, modifier = surfaceModifier, color = container, contentColor = content, content = body)
    } else {
        Surface(modifier = surfaceModifier, color = container, contentColor = content, content = body)
    }
}
