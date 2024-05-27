package com.example.downloadapp

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.downloadapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val networkInformation: NetworkInformation = NetworkInformation(0, 0, false)
    private var wasConnected = false
    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkInformation.isConnected = true
                runOnUiThread { updateNetworkInformationScreen() }
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                networkInformation.isConnected = true
                networkInformation.downSpeed = networkCapabilities.linkDownstreamBandwidthKbps
                networkInformation.upSpeed = networkCapabilities.linkUpstreamBandwidthKbps
                runOnUiThread { updateNetworkInformationScreen() }
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                networkInformation.isConnected = false
                wasConnected = false
                runOnUiThread { updateNetworkInformationScreen() }
            }
        }
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)

    }


    private fun updateNetworkInformationScreen() {
        // update the UI with the network information
        if(networkInformation.error != null) {
            showSnackbar(networkInformation.error!!)
            binding.txtDownloadSpeed.text = "Download Speed: 0Kbps"
            binding.txtUploadSpeed.text = "Upload Speed: 0Kbps"
            return
        }
        else if (!networkInformation.isConnected) {
            showSnackbar("Network not connected")
            binding.txtDownloadSpeed.text = "Download Speed: 0Kbps"
            binding.txtUploadSpeed.text = "Upload Speed: 0Kbps"
            return
        } else {
            if (!wasConnected) {
                showSnackbar("Network connected")
                wasConnected = true
            }
            binding.txtDownloadSpeed.text = "Download Speed: ${networkInformation.downSpeed}Kbps"
            binding.txtUploadSpeed.text = "Upload Speed: ${networkInformation.upSpeed}Kbps"
        }
    }

    private fun showSnackbar(message: String) {
        // show snackbar
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}
