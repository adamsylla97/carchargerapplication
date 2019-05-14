package com.example.carchargerapplication

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.control_layout.*
import java.io.BufferedReader
import java.io.IOException
import android.content.Context.VIBRATOR_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.support.v4.content.ContextCompat.getSystemService
import com.google.gson.Gson


class MyThread : Thread() {

    private var i : Int = 0
    var wait: Boolean = false
    lateinit var  progressBarBatteryLevel: ProgressBar
    lateinit var batteryLevelTextView: TextView
    lateinit var myActivity: Activity
    lateinit var myContext: Context
    lateinit var vibe: Vibrator

    companion object{
        private var myThread: MyThread? = null

        fun getInstance(): MyThread{
            if(myThread != null){
                return myThread!!
            }else{
                myThread = MyThread()
                return myThread!!
            }
        }
    }

    private fun sendCommand(input: String){
        if(ControlActivity.m_bluetoothSocket != null){
            try {
                ControlActivity.m_bluetoothSocket!!.outputStream.write(input.toByteArray())

                Log.i("data 123 123","sended " + input)

            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun getData(){
        var data: BufferedReader
        if(ControlActivity.m_bluetoothSocket != null){
            try{
                data = ControlActivity.m_bluetoothSocket!!.inputStream.bufferedReader(Charsets.US_ASCII)
                Log.i("data","1")
                var temp: String = data.readLine()
                if(temp.length > 0){

                    Log.i("data get data",temp)
                    processJson(temp)

                } else {

                    Log.i("data","pusto")

                }
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun run() {
        //var controlActivity: ControlActivity = ControlActivity()
        while(true) {
            if(!wait) {
                sleep(1000)
                //controlActivity.getData()
                if (i < 100)
                    i ++

                Log.i("data i " ,i.toString())

                if(i == 10){
                    sendCommand("2")
                }

                if(i == 15){
                    getData()
                }

                if(i == 20){
                    sendCommand("2")
                }

                if(i == 25){
                    getData()
                }

                if(i == 100){
                    vibe.vibrate(3000)
                }

                myActivity.runOnUiThread(Runnable {

                        Log.i("battery level thread",progressBarBatteryLevel.progress.toString())
                        progressBarBatteryLevel.setProgress(i,true)
                        batteryLevelTextView.text = i.toString();

                        if(i >= 100){
                            batteryLevelTextView.text = "Naładowane"
                        }

                })
            }
        }
    }

    fun processJson(json: String): String{
        var temp: List<String> = json.split(":")
        var i = 0
        var index = 0
        while(i<temp.size){
            if(temp[i].contains("result")){
                Log.i("data index",index.toString())
            }
        }
        return index.toString()
    }

    fun getBatteryLevel(): Int = i

    fun go(activity: Activity, context: Context){
        myActivity = activity
        myContext = context
        progressBarBatteryLevel = activity.findViewById(R.id.progressBarBatteryLevel)
        batteryLevelTextView = activity.findViewById(R.id.batteryTextView)
        vibe = myActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(!isAlive)
            myThread!!.start()
    }
}