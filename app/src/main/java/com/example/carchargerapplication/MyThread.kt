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
import android.graphics.Color
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.control_layout.*


class MyThread : Thread() {

    private var i : Int = 0
    var wait: Boolean = false
//    lateinit var  progressBarBatteryLevel: ProgressBar
//    lateinit var batteryLevelTextView: TextView
    lateinit var myActivity: Activity
    lateinit var myContext: Context
    lateinit var vibe: Vibrator

//    var builder = NotificationCompat.Builder(myContext)
//        .setContentTitle("temp")
//        .setContentText("alalalala")
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

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

    private fun sendCommand(input: Byte){
        if(ControlActivity.m_bluetoothSocket != null){
            try {
                val byteArray: ByteArray = ByteArray(1)
                byteArray.set(0, input)
                ControlActivity.m_bluetoothSocket!!.outputStream.write(byteArray)

                Log.i("data 123 123","sended " + input)

            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun getData(): String{
        var data: BufferedReader
        if(ControlActivity.m_bluetoothSocket != null){
            try{
                data = ControlActivity.m_bluetoothSocket!!.inputStream.bufferedReader(Charsets.US_ASCII)
                Log.i("data","1")
                var temp: String = data.readLine()
                if(temp.length > 0){

                    Log.i("data get data",temp)

                    //processJson(temp)
                    return temp

                } else {

                    Log.i("data","pusto")

                }
            }catch (e: IOException){
                e.printStackTrace()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return "";
    }

    fun checkIfCanLoad(): Boolean{
        sendCommand(102.toByte())
        //send tag
        sendCommand(104.toByte())
        var data = getData()
        Log.i("ladowanie",data)
        //"dataShape":{"fieldDefinitions"{"result":{"name":"result","desption":"","baseType":"NUMBER"ordinal":0,"aspects":{}}}},"ro":[{"result":1.0}]}
        var result = data.split(":")
        Log.i("result",result[9])
        if(result[9].toCharArray()[0]=='1'){
            return true
        } else {
            return false
        }
    }

    override fun run() {

        var canLoad = false

        //var controlActivity: ControlActivity = ControlActivity()
        if(checkIfCanLoad()){
            canLoad = true
            while(canLoad) {
                if(!wait) {
                    sleep(1000)


                    if (i < 100)
                        i ++

                    Log.i("data i " ,i.toString())

                    sendCommand(i.toByte())

//                    var data: String = getData().toString()
//
//                    Log.i("getdata",data)

                    if(i == 100){
                        vibe.vibrate(3000)
                    }

                    myActivity.runOnUiThread(Runnable {

                        myActivity.batteryLevel.chargeLevel = i

                    })
                }
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
        //progressBarBatteryLevel = activity.findViewById(R.id.progressBarBatteryLevel)
        //batteryLevelTextView = activity.findViewById(R.id.batteryTextView)
        with(myActivity.batteryLevel){
            chargeLevel = 0
            criticalChargeLevel = 15
            color = Color.GREEN
            isCharging = false
        }
        vibe = myActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(!isAlive)
            myThread!!.start()
    }
}