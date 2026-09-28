package com.naze.maps.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface ConnectivityObserver {
    fun isOnlineNow(): Boolean
    fun observe(): Flow<Boolean>
}

/**
 * Reports whether the device currently has usable internet, used to drive the
 * "Internet error" state (search / routing / tiles need network; core map pan/zoom doesn't).
 */
class ConnectivityObserverImpl(context: Context) : ConnectivityObserver {
    private val cm = context.applicationContext.getSystemService<ConnectivityManager>()

    override fun isOnlineNow(): Boolean {
        val network = cm?.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun observe(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(isOnlineNow()) }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            }
        }
        val request = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm?.registerNetworkCallback(request, callback)
        trySend(isOnlineNow())
        awaitClose { cm?.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
