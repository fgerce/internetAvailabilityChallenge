package com.example.downloadapp

data class NetworkInformation(
    var downSpeed: Int,
    var upSpeed: Int,
    var isConnected: Boolean,
    var error: String? = null
)
