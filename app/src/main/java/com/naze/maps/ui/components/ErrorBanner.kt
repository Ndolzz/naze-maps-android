package com.naze.maps.ui.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.naze.maps.R
import com.naze.maps.ui.screens.MapBanner

@Composable
fun ErrorBanner(banner: MapBanner, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val message = when (banner) {
        MapBanner.GpsOff -> stringResource(context, R.string.err_gps_off)
        MapBanner.PermissionDenied -> stringResource(context, R.string.err_permission_denied)
        MapBanner.NoInternet -> stringResource(context, R.string.err_internet)
        is MapBanner.Generic -> banner.message
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(message, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            if (banner == MapBanner.PermissionDenied || banner == MapBanner.GpsOff) {
                TextButton(onClick = {
                    val action = if (banner == MapBanner.GpsOff) {
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    } else {
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    }
                    context.startActivity(Intent(action).apply {
                        if (action == Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
                            data = android.net.Uri.fromParts("package", context.packageName, null)
                        }
                    })
                }) { Text(stringResource(context, R.string.action_open_settings)) }
            }
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    }
}

private fun stringResource(context: android.content.Context, resId: Int): String =
    context.getString(resId)
