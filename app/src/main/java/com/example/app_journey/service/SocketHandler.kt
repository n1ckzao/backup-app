package com.example.app_journey.service

import io.socket.client.IO
import io.socket.client.Socket

object SocketHandler {
    private lateinit var socket: Socket

    fun initSocket(serverUrl: String) {
        socket = IO.socket(serverUrl)
    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun getSocket(): Socket {
        return socket
    }
}