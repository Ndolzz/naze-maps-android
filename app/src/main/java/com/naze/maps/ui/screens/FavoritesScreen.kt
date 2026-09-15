package com.naze.maps.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val favorites by viewModel.favorites.collectAsState(initial = emptyList())

    if (favorites.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
        ) {
            Text("Belum ada lokasi tersimpan", style = MaterialTheme.typography.titleMedium)
            Text(
                "Simpan lokasi dari hasil pencarian di peta untuk melihatnya di sini.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(favorites, key = { it.id }) { fav ->
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(fav.name, style = MaterialTheme.typography.bodyLarge)
                        Text(fav.address, style = MaterialTheme.typography.labelSmall)
                    }
                    Row {
                        Icon(Icons.Filled.Place, contentDescription = null)
                        IconButton(onClick = {
                            viewModel.deleteFavoriteFromScreen(fav)
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            }
        }
    }
}
