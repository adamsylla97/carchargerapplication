package com.example.carchargerapplication

import android.app.Activity
import android.util.Log
import android.widget.ProgressBar

class MyThread : Thread() {

    private var i : Int = 0
    var wait: Boolean = false
    lateinit var  progressBarBatteryLevel: ProgressBar
    lateinit var myActivity: Activity

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
    override fun run() {
        while(true) {
            if(!wait) {
                sleep(1000)
                if (i < 100)
                    i ++
                println("i = $i")
                myActivity.runOnUiThread(Runnable {

                        Log.i("battery level thread",progressBarBatteryLevel.progress.toString())
                        progressBarBatteryLevel.setProgress(i,true)
                        progressBarBatteryLevel.incrementProgressBy(1)

                })
            }
        }
    }

    fun getBatteryLevel(): Int = i

    fun go(activity: Activity){
        myActivity = activity
        progressBarBatteryLevel = activity.findViewById(R.id.progressBarBatteryLevel)
        if(!isAlive)
            myThread!!.start()




    }
}