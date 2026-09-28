package com.naze.maps.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * CH-112: halaman bantuan statis — menjelaskan cara pakai fitur yang benar benar ada.
 * Tidak menyebut kemampuan yang tidak tersedia (anti fitur fiktif).
 */
@Composable
fun HelpScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text("Bantuan", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(Modifier.height(16.dp))

        HelpTopic(
            icon = Icons.Filled.Search,
            title = "Cari tempat",
            body = "Ketik nama tempat pada kotak pencarian di tab Peta. Saran dari riwayat muncul lebih dulu, hasil pencarian online menyusul beberapa saat kemudian.",
        )
        HelpTopic(
            icon = Icons.Filled.DirectionsCar,
            title = "Rute ke tempat",
            body = "Pilih tempat, lalu pilih mode Mobil, Jalan kaki, atau Sepeda. Garis rute digambar di peta bersama kartu ringkasan jarak dan perkiraan waktu. Aktifkan My Location dulu agar titik awal rute diketahui.",
        )
        HelpTopic(
            icon = Icons.Filled.Favorite,
            title = "Favorit",
            body = "Tekan lama titik mana pun di peta untuk menyimpannya sebagai favorit. Di tab Favorit ada tombol Rute ke sini untuk langsung membuat rute, dan nama favorit bisa diubah.",
        )
        HelpTopic(
            icon = Icons.Filled.History,
            title = "Riwayat",
            body = "Tempat yang pernah dipilih tersimpan otomatis sebagai riwayat. Riwayat bisa dihapus per baris atau semuanya sekaligus.",
        )
        HelpTopic(
            icon = Icons.Filled.DarkMode,
            title = "Tema aplikasi",
            body = "Buka Pengaturan lalu pilih Tema aplikasi: Terang, Gelap, atau Ikuti sistem untuk mengikuti mode perangkat.",
        )
        HelpTopic(
            icon = Icons.Filled.Straighten,
            title = "Satuan jarak",
            body = "Buka Pengaturan lalu pilih Satuan jarak: Kilometer atau Miles. Pilihan ini dipakai pada kartu rute dan kalkulator jarak.",
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun HelpTopic(icon: ImageVector, title: String, body: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
