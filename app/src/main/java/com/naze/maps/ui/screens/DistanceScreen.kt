package com.naze.maps.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naze.maps.search.SearchOutcome
import com.naze.maps.search.SearchRepository
import com.naze.maps.utils.DistanceUtils
import com.naze.maps.utils.GeoPoint
import com.naze.maps.utils.TravelMode
import kotlinx.coroutines.launch

private data class DcPoint(val label: String, var value: String = "")

@Composable
fun DistanceScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val searchRepo = remember { SearchRepository() }

    val points = remember { mutableStateListOf(DcPoint("A"), DcPoint("B")) }
    var travelMode by remember { mutableStateOf(TravelMode.DRIVING) }
    var isCalculating by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Distance Calculator", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            items(points.size) { i ->
                OutlinedTextField(
                    value = points[i].value,
                    onValueChange = { points[i] = points[i].copy(value = it) },
                    label = { Text("Titik ${points[i].label}") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true,
                )
            }
        }

        TextButton(onClick = {
            val nextLabel = ('A' + points.size).toString()
            points.add(DcPoint(nextLabel))
        }) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Tambah titik")
        }

        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            TravelMode.values().forEach { mode ->
                TextButton(onClick = { travelMode = mode }) {
                    Text(mode.name, fontWeight = if (mode == travelMode) androidx.compose.ui.text.font.FontWeight.Bold else null)
                }
            }
        }

        Button(
            onClick = {
                val filled = points.filter { it.value.isNotBlank() }
                if (filled.size < 2) {
                    errorText = "Isi minimal 2 titik"
                    return@Button
                }
                isCalculating = true
                errorText = null
                resultText = null
                scope.launch {
                    val resolved = mutableListOf<GeoPoint>()
                    for (p in filled) {
                        val loc = state.myLocation
                        if (p.value.trim().equals("lokasi saya", ignoreCase = true) && loc != null) {
                            resolved.add(GeoPoint(loc.latitude, loc.longitude, p.label))
                            continue
                        }
                        val geocoded = searchRepo.geocodeOne(p.value)
                        if (geocoded == null) {
                            errorText = "Tidak ditemukan: ${p.value}"
                            isCalculating = false
                            return@launch
                        }
                        resolved.add(GeoPoint(geocoded.latitude, geocoded.longitude, p.label))
                    }
                    var totalKm = 0.0
                    for (i in 0 until resolved.size - 1) {
                        totalKm += DistanceUtils.haversineKm(resolved[i], resolved[i + 1])
                    }
                    val minutes = DistanceUtils.etaMinutes(totalKm, travelMode)
                    resultText = "${DistanceUtils.format(totalKm, state.distanceUnit)} • $minutes menit"
                    isCalculating = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Hitung Jarak") }

        if (isCalculating) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        errorText?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }
        resultText?.let { text ->
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(text, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
