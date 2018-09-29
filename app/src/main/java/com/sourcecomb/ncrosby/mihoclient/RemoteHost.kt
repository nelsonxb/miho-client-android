package com.sourcecomb.ncrosby.mihoclient

import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import java.net.Socket
import java.util.concurrent.Future

private val hexArray = "0123456789ABCDEF".toCharArray()
fun ByteArray.toHex(): String {
    val hexChars = CharArray(this.size * 2)
    for (j in this.indices) {
        val v = this[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v.ushr(4)]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}

class RemoteHost(hostname: String) {
    private lateinit var socket: Socket
    private var thread: Future<Unit>

    init {
        thread = doAsync {
            socket = Socket(hostname, 1234)
            socket.tcpNoDelay = true
        }
    }

    fun sendMouseMove(dx: Int, dy: Int): Future<Unit> {
        val buf = ByteArray(3)
        buf[0] = 3
        buf[1] = dx.toByte()
        buf[2] = dy.toByte()

        thread = thread.doAsyncResult {
            Log.d("RemoteHost", "Writing mouse move ${buf.toHex()}")
            val stream = socket.getOutputStream()
            stream.write(buf)
            stream.flush()
        }

        return thread
    }
}