package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.*

class CoroutinePractice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_practice)

        findViewById<Button>(R.id.btn_counter).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(CoroutinePractice::class.simpleName, "onCreate:IO: ${Thread.currentThread().name}")
            }
            GlobalScope.launch(Dispatchers.Main) {
                Log.d(CoroutinePractice::class.simpleName, "onCreate: Main: ${Thread.currentThread().name}")
            }
            MainScope().launch(Dispatchers.Default) {
                Log.d(CoroutinePractice::class.simpleName, "onCreate: Default: ${Thread.currentThread().name}")
            }
        }

        // Suspention Test..//
        CoroutineScope(Dispatchers.Main).launch {
            task1()
        }

        CoroutineScope(Dispatchers.Main).launch {
            task2()
        }
        
        //coroutineBuilders()
        //asynAwaitExample1()
        asynAwaitExample2()

//        simpleCoroutine()
//        main()
//        launchCoroutines()
//        dependentCoroutines()
//        jobHeirarchy()
//        asynAwaitExample()

    }

    private fun asynAwaitExample1() {
        Log.d(CoroutinePractice::class.simpleName, "asynAwaitExample1: ")
        CoroutineScope(Dispatchers.Main).launch {
            // Async return the data type of last statement. //
            val job = CoroutineScope(Dispatchers.IO).async {
                getFollowers()
            }
            Log.d(CoroutinePractice::class.simpleName, "printFollowers: ${job.await()}")
        }

    }
    private fun asynAwaitExample2() {
        Log.d(CoroutinePractice::class.simpleName, "asynAwaitExample1: ")
        CoroutineScope(Dispatchers.IO).launch {
            // Async return the data type of last statement. //
            val job = CoroutineScope(Dispatchers.IO).async {
                getFollowers()
            }
            val job1 = CoroutineScope(Dispatchers.IO).async {
                getInstaFollowers()
            }
            Log.d(CoroutinePractice::class.simpleName, "printFollowers: ${job.await()} ${job1.await()}")
        }

    }


    private fun coroutineBuilders() {
        Log.d(CoroutinePractice::class.simpleName, "coroutineBuilders: ")
        CoroutineScope(Dispatchers.Main).launch {
            printFollowers()
        }

    }

    suspend fun printFollowers() {
        Log.d(CoroutinePractice::class.simpleName, "printFollowers: ")
        var fbFollower = 0
        val job = CoroutineScope(Dispatchers.IO).launch {
            fbFollower = getFollowers()
        }
        // join is used to wait for the coroutine to finish().
        // coroutine is remain in the suspended state.
        job.join()
        Log.d(CoroutinePractice::class.simpleName, "printFollowers: $fbFollower")
    }

    suspend fun getFollowers():Int{
        delay(1000)
        return 100
    }
    suspend fun getInstaFollowers():Int{
        delay(1000)
        return 120
    }


    suspend fun task1(){
        Log.d(CoroutinePractice::class.simpleName, "task1: Started")
        // its a suspention point for coroutine..//
       yield()
        Log.d(CoroutinePractice::class.simpleName, "task1: End")
    }

    suspend fun task2(){
        Log.d(CoroutinePractice::class.simpleName, "task2: Started")
        yield()
        Log.d(CoroutinePractice::class.simpleName, "task2: End")
    }


    suspend fun fetchUserData(): String {
        delay(1000) // Simulate fetching user data from a server
        return "User Data"
    }

    suspend fun fetchProductData(): String {
        delay(1500) // Simulate fetching product data from a server
        return "Product Data"
    }

    fun asynAwaitExample() = runBlocking {
        val userDeferred = async { fetchUserData() }
        val productDeferred = async { fetchProductData() }

        val userData = userDeferred.await()
        val productData = productDeferred.await()

        println("User data: $userData")
        println("Product data: $productData")
    }


    fun dependentCoroutines() {
        val job1 = GlobalScope.launch(start = CoroutineStart.LAZY) {
            delay(200)
            println("Pong")
            delay(200)
        }
        GlobalScope.launch {
            delay(200)
            println("Ping")
            job1.join()
            println("Ping")
            delay(200)
        }
        Thread.sleep(1000)
    }

    private fun simpleCoroutine() {
        GlobalScope.launch {
            println("Hello coroutine!")
            delay(500)
            println("Right back at ya!")
        }
        Thread.sleep(1000)
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

    fun jobHeirarchy() {
        with(GlobalScope) {
            val parentJob = launch {
                delay(200)
                println("I’m the parent")
                delay(200)
            }
            launch(context = parentJob) {
                delay(200)
                println("I’m a child")
                delay(200)
            }
            if (parentJob.children.iterator().hasNext()) {
                println("The Job has children ${parentJob.children}")
            } else {
                println("The Job has NO children")
            }
            Thread.sleep(1000)
        }
    }
}