package com.example.carchargerapplication

class MyThread : Thread() {

    private var i : Int = 0
    var wait: Boolean = false

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
                    i++
                println("i = $i")
            }
        }
    }

    fun getBatteryLevel(): Int = i

    fun go(){
        if(!isAlive)
            myThread!!.start()
    }
}