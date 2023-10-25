package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SharedFlowPractice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_flow_practice)
        GlobalScope.launch(Dispatchers.Main) {
            val result = producer()
            result.collect{
                Log.d(SharedFlowPractice::class.simpleName, "onCreate: item: $it")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val result = producer()
            delay(1000)
            result.collect{
                Log.d(SharedFlowPractice::class.simpleName, "onCreate: item1: $it")
            }
        }
    }

    private fun producer() : Flow<Int> {
            val mutableSharedFlow = MutableSharedFlow<Int>(1)
        GlobalScope.launch {
            val list = listOf(1, 2, 3, 4, 5)
            list.forEach {
                mutableSharedFlow.emit(it)
                delay(1000)
            }
        }
        return mutableSharedFlow
    }

}