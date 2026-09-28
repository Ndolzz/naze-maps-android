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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naze.maps.data.ThemeMode
import com.naze.maps.utils.DistanceUnit

/**
 * CH-104 (BUG-020): redesain layar Pengaturan — profesional, terkelompok per bagian
 * (Tampilan, Peta, Tentang), ikon per baris, dan tanpa karakter hubung pada seluruh
 * teks UI. Isi tetap berbasis fitur yang benar benar tersedia (tema, satuan jarak,
 * info versi dari manifest) — tidak ada fitur fiktif.
 * CH-111: baris Tema gelap dengan sakelar diganti tiga pilihan mode tema
 * (Terang, Gelap, Ikuti sistem).
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text("Pengaturan", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(20.dp))

        SectionHeader(text = "Tampilan")
        SettingsCard {
            Column {
                SettingsRow(
                    icon = Icons.Filled.DarkMode,
                    title = "Tema aplikasi",
                    subtitle = "Terang, gelap, atau ikuti pengaturan sistem",
                    trailing = {},
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ThemeOption(
                        label = "Terang",
                        selected = state.settings.themeMode == ThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                    )
                    ThemeOption(
                        label = "Gelap",
                        selected = state.settings.themeMode == ThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                    )
                    ThemeOption(
                        label = "Ikuti sistem",
                        selected = state.settings.themeMode == ThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionHeader(text = "Peta")
        SettingsCard {
            Column {
                SettingsRow(
                    icon = Icons.Filled.Straighten,
                    title = "Satuan jarak",
                    subtitle = "Dipakai untuk rute dan kalkulator jarak",
                    trailing = {},
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    UnitOption(
                        label = "Kilometer",
                        selected = state.settings.distanceUnit == DistanceUnit.KM,
                        onClick = { viewModel.setDistanceUnit(DistanceUnit.KM) },
                    )
                    UnitOption(
                        label = "Miles",
                        selected = state.settings.distanceUnit == DistanceUnit.MI,
                        onClick = { viewModel.setDistanceUnit(DistanceUnit.MI) },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionHeader(text = "Tentang")
        SettingsCard {
            SettingsRow(
                icon = Icons.Filled.Place,
                title = "NAZE Maps",
                subtitle = "Menjelajah dunia lewat peta terbuka",
                trailing = {},
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsRow(
                icon = Icons.Filled.Info,
                title = "Versi aplikasi",
                subtitle = versionName,
                trailing = {},
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp,
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing()
    }
}

// CH-111: pil pilihan mode tema — pola sama seperti UnitOption satuan jarak.
@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun UnitOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
        )
    }
}
