package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.log

class FlowPractice:AppCompatActivity() {
    var flowOfString : Flow<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(FlowPractice::class.simpleName, "onCreate: ")
        setContentView(R.layout.activity_channel_practice)

        // Flow Operator Practice. //

        // Producer..//
        val myFlow = flow {
            emit("hello world.")
            for(i in 1..10){
                emit("New value is $i")
                delay(1000)
            }
        }

        // consumer...//
        GlobalScope.launch(Dispatchers.IO){
            myFlow.buffer().collect{
                Log.d(FlowPractice::class.simpleName, "onCreate Flow: $it Thread is ${Thread.currentThread().name}")
                delay(2000)
            }
        }

        GlobalScope.launch {
            producer().map {
                it*2
            }.filter {
                it>4
            }.collect{
                Log.d(FlowPractice::class.simpleName, "onCreate: Value is $it")
            }

        }

        GlobalScope.launch {
            val firstElement = producer().first()
            Log.d(FlowPractice::class.simpleName, "onCreate: First Element of the Flow..")
            val data:Flow<Int> = producer().onStart {
                emit(-1)
                Log.d(FlowPractice::class.simpleName, "onCreate: Flow Started Emitting")
            }.onCompletion {
                emit(100)
                Log.d(FlowPractice::class.simpleName, "onCreate: Flow Completed")
                }
                .onEach {
                    Log.d(FlowPractice::class.simpleName, "onCreate: New Item Emitted..")
                }

            delay(3000)
            data.collect{
                Log.d(FlowPractice::class.simpleName, "onCreate: Consumer2 $it")
            }

        }

        GlobalScope.launch {
            val data:Flow<Int> = producer()
            data.collect{
                Log.d(FlowPractice::class.simpleName, "onCreate: Consumer1 $it")
            }

        }

        GlobalScope.launch {
            val data:Flow<String> = stringProducer()
            data.collect{
                Log.d(FlowPractice::class.simpleName, "onCreate: Consumer3 Collect $it")
            }

        }





//        flowOfString = flow<String> {
//            for(i in 0..100){
//                emit("Emiting: $i")
//            }
//        }

//        GlobalScope.launch {
//            flowOfString?.map { it.split(" ") }
//                ?.map { it.last() }
//                ?.flowOn(Dispatchers.IO)
//                ?.onEach { delay(100) }
//                ?.flowOn(Dispatchers.Main)
//                ?.collect { value ->
//                    println(value)
//                }
//        }

    GlobalScope.launch {
        flowOfString
            ?.map { it.split(" ") }
            ?.map { it[0] }
            ?.onEach { delay(100) }
            ?.catch { it.printStackTrace() }
            ?.flowOn(Dispatchers.Default)
            ?.collect { println(it) }

    }
        Thread.sleep(1000)
    }

    private fun producer()= flow {
        val list = listOf<Int>(1,2,3,4)
        list.forEach{
            delay(1000)
            emit(it)
        }
    }

    private fun stringProducer() = flow<String> {
        val list = listOf<String>("test","test1","test2","test3","test4","test5")
        list.forEach{
            delay(100)
            emit(it)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(FlowPractice::class.simpleName, "onStart: ")

    }





}