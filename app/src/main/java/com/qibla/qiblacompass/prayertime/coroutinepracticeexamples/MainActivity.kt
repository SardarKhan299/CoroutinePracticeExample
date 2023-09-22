package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main()
        launchCoroutines()
    }

    fun main() = runBlocking {
        val job = launch {
            delay(1000)
            Log.d(MainActivity::class.simpleName, "main: Hello From Coroutine...")
        }
        job.join()
    }

    fun launchCoroutines() {
        (1..10000).forEach {
            GlobalScope.launch {
                val threadName = Thread.currentThread().name
                println("$it printed on thread ${threadName}")
            }
        }
        Thread.sleep(1000)
    }


}