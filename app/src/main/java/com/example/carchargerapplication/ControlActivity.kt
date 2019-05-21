package com.example.carchargerapplication

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.control_layout.*
import java.io.BufferedReader
import java.io.IOException
import java.util.*

class ControlActivity: AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_adress: String
        lateinit var myActivity: Activity
        lateinit var myContext: Context
    }

    var myThread = MyThread.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_adress = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

        //call inner class
        ConnectToDevice(this).execute()
        disconnectButton.setOnClickListener {
            disconnect()
        }

        var stopButton: Button = findViewById(R.id.stopDataButton)
        var startButton: Button = findViewById(R.id.startButton)

        stopButton.setOnClickListener {
            stop()
        }

        startButton.setOnClickListener {
            start()
        }

    }

    private fun sendCommand(input: String){
        if(m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    progressBarBatteryLevel.setProgress(progressBarBatteryLevel.progress - 10, true)
//                }else{
//                    progressBarBatteryLevel.progress -= 10
//                }

                //batteryTextView.text = progressBarBatteryLevel.progress.toString() + "%"

            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
                myThread.wait = true
            } catch (e: IOException){
                e.printStackTrace()
            }
            finish()
        }
    }

    private fun stop(){
        if(m_bluetoothSocket != null){
            try{
                myThread.wait = true
                myThread.sendCommand(105.toByte())
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun start(){
        if(m_bluetoothSocket != null){
            try{
                myThread.wait = false
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun getData(){
        var data: BufferedReader
        if(m_bluetoothSocket != null){
            try{
                data = m_bluetoothSocket!!.inputStream.bufferedReader(Charsets.US_ASCII)
                Log.i("data","1")
                var temp: String = data.readLine()
                if(temp.length > 0){
                    Log.i("data","2")
                    Log.i("data",temp)
                    Log.i("data","3")
                } else {
                    Log.i("data","4")
                    Log.i("data","pusto")
                    Log.i("data","5")
                }
                Log.i("data","6")
                Log.i("data","7")
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private inner class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){

        private var connectSuccess: Boolean = true
        private val context: Context

        init{
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg params: Void?): String? {
            try{

                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_adress)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery() //stops looking for other devices (saving battery and resources)
                    m_bluetoothSocket!!.connect()
                }

            } catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","couldn't connect")
                finish()
            } else {
                m_progress.dismiss()
                myActivity = this@ControlActivity
                myContext = this.context
                myThread.go(myActivity, myContext)
                myThread.wait = false
            }
        }

    }

}