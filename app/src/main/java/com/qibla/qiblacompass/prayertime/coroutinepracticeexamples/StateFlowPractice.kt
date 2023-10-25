package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StateFlowPractice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_flow)
        GlobalScope.launch (Dispatchers.IO){
            val result = producer()
            Log.d(StateFlowPractice::class.simpleName, "onCreate: ${result.value}")
            result.collect{
                Log.d(StateFlowPractice::class.simpleName, "onCreate: Collect $it")
            }
        }

    }

    private fun producer(): StateFlow<Int> {
        val mutableStateFlow = MutableStateFlow(10)
        GlobalScope.launch {
            val list = listOf<Int>(1,2,3)
            list.forEach{
                Log.d(StateFlowPractice::class.simpleName, "producer: Emiting $it")
                delay(2000)
                mutableStateFlow.emit(it)
            }
        }
        return mutableStateFlow
    }

}