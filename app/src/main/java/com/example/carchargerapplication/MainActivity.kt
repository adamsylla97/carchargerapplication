package com.example.carchargerapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent){
            var action = intent.action

            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    var device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var superDevice = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE)
                    val deviceName = device.name
                    val deviceAddress = device.address
                    Log.d("FOUND",deviceName + " " + deviceAddress + " " + superDevice)
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> Log.d("STARTED", " ")
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> Log.d("FINISHED", " ")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    fun btnClick(view: View){
        bluetoothAdapter.startDiscovery()

        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        registerReceiver(broadcastReceiver, intentFilter)
    }


}
