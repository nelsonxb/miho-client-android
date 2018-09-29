package com.sourcecomb.ncrosby.mihoclient

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
class SimpleTrackpadActivity : AppCompatActivity() {

    lateinit var remoteHost: RemoteHost

    private var lastX: Int = 0
    private var lastY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_trackpad)

        remoteHost = RemoteHost(resources.getString(R.string.default_host))

        findViewById<View>(R.id.trackpad).setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("SimpleTrackpad", "Registered touch start")
                    lastX = motionEvent.x.toInt()
                    lastY = motionEvent.y.toInt()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = motionEvent.x.toInt()
                    val newY = motionEvent.y.toInt()
                    val x = (newX - lastX)
                    val y = (newY - lastY)
                    lastX = newX
                    lastY = newY
                    Log.d("SimpleTrackpad", "Moving mouse ($x, $y)")
                    remoteHost.sendMouseMove(x, y)
                    true
                }
                else -> false
            }
        }

        val btnOnTouch = { buttonID: Int ->
            { view: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("SimpleTrackpad", "Pressing mouse $buttonID")
                        remoteHost.sendMouseButton(buttonID, true)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        view.performClick()
                        Log.d("SimpleTrackpad", "Releasing mouse $buttonID")
                        remoteHost.sendMouseButton(buttonID, false)
                        true
                    }
                    else -> false
                }
            }
        }

        findViewById<Button>(R.id.mouse_left).setOnTouchListener(btnOnTouch(1))
        findViewById<Button>(R.id.mouse_right).setOnTouchListener(btnOnTouch(2))
        findViewById<Button>(R.id.mouse_middle).setOnTouchListener(btnOnTouch(3))
    }
}

