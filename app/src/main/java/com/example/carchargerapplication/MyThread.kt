package com.example.carchargerapplication

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
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
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.Button
import com.google.gson.Gson
import kotlinx.android.synthetic.main.control_layout.*
import org.jetbrains.anko.find


class MyThread : Thread() {

    private var i : Int = 0
    var wait: Boolean = false
    lateinit var myActivity: Activity
    lateinit var myContext: Context
    lateinit var vibe: Vibrator
    var notificationManager: NotificationManager? = null
    lateinit var stopButton: Button
    lateinit var startButton: Button

    var builder: NotificationCompat.Builder? = null

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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ChanelName"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("id", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager = myActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun sendCommand(input: Byte){
        if(ControlActivity.m_bluetoothSocket != null){
            try {

                if(input == 105.toByte()){
                    sleep(3000)
                }

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

    var canLoad = false

    fun stopCharging(){
        sendCommand(105.toByte())
        canLoad = false
        myThread = null
    }

    fun startCharging(){
        canLoad = true
        //Log.i("thread",myThread!!.state.toString())
        myThread = getInstance()
        go(ControlActivity.myActivity,ControlActivity.myContext)
    }

    override fun run() {

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

                    if(i%10 == 0){
                        var data: String = getData().toString()
                        Log.i("getdata",data)
                        var result = data.split(":")
                        if(result[9].toCharArray()[0]=='0'){
                            canLoad = false
                        }
                    }

                    if(i == 100){
                        vibe.vibrate(3000)
                        canLoad = false

                    }

                    myActivity.runOnUiThread(Runnable {

                        myActivity.batteryLevel.isCharging = true
                        myActivity.batteryLevel.chargeLevel = i
                        if(i == 100 || canLoad == false){
                            Log.i("battery","jestem tutaj xd")
                            myActivity.batteryLevel.isCharging = false

                            with(NotificationManagerCompat.from(myActivity)){
                                notify(1,builder!!.build())
                            }

                        }

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
            color = Color.rgb(0,150,45)
            isCharging = false
        }

        vibe = myActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        builder = NotificationCompat.Builder(myContext,"id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Car charger application")
            .setContentText("Zakończono ładowanie")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        createNotificationChannel()

        if(!isAlive)
            myThread!!.start()
    }
}