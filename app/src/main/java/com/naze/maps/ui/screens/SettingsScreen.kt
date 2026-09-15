package com.naze.maps.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naze.maps.utils.DistanceUnit

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text("Pengaturan", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Tema gelap", style = MaterialTheme.typography.bodyLarge)
                Text("Sesuai identitas brand Naze (dark modern)", style = MaterialTheme.typography.labelSmall)
            }
            Switch(checked = state.isDarkTheme, onCheckedChange = { viewModel.toggleTheme() })
        }

        Text("Satuan jarak", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
        Row(modifier = Modifier.padding(top = 6.dp)) {
            TextButton(onClick = { viewModel.setDistanceUnit(DistanceUnit.KM) }) {
                Text("Kilometer", fontWeight = if (state.distanceUnit == DistanceUnit.KM) androidx.compose.ui.text.font.FontWeight.Bold else null)
            }
            TextButton(onClick = { viewModel.setDistanceUnit(DistanceUnit.MI) }) {
                Text("Miles", fontWeight = if (state.distanceUnit == DistanceUnit.MI) androidx.compose.ui.text.font.FontWeight.Bold else null)
            }
        }
    }
}
