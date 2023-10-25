package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StateFlow : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_flow)
        GlobalScope.launch (Dispatchers.IO){
            val result = producer()
            delay(8000)
            result.collect{
                Log.d(StateFlow::class.simpleName, "onCreate: Collect $it")
            }
        }

    }

    private fun producer(): Flow<Int> {
        val mutableStateFlow = MutableStateFlow(10)
        GlobalScope.launch {
            val list = listOf<Int>(1,2,3)
            list.forEach{
                Log.d(StateFlow::class.simpleName, "producer: Emiting $it")
                delay(2000)
                mutableStateFlow.emit(it)
            }
        }
        return mutableStateFlow
    }

}