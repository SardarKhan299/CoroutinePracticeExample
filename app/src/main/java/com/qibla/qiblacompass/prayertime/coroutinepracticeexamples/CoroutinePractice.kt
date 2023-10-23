package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce

class CoroutinePractice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_practice)
        //startActivity(Intent(this,ChannelPractice::class.java))

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
//        CoroutineScope(Dispatchers.Main).launch {
//            task1()
//        }
//
//        CoroutineScope(Dispatchers.Main).launch {
//            task2()
//        }
        
        coroutineBuilders()
        //asynAwaitExample1()
        //asynAwaitExample2()
        //coroutineHierarchy()
        //coroutineCancellation()
        //withContextExample()

//        simpleCoroutine()
          //main()
//        launchCoroutines()
//        dependentCoroutines()
//        jobHeirarchy()
//        asynAwaitExample()

    }



    private fun withContextExample() {
        Log.d(CoroutinePractice::class.simpleName, "withContextExample: ")
        GlobalScope.launch {
            executeTaskWithContext()
        }

    }



    private suspend fun executeTaskWithContext() {
        Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: Before")
        GlobalScope.launch {
            Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: inside")
        }
        Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: After")
        // Now same Example with withContext ..//
        Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: Before WithContext")
        // coroutine will be suspended and next line will not execute before its finished..//
        withContext(Dispatchers.IO) {
            Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: inside WithContext")
        }
        Log.d(CoroutinePractice::class.simpleName, "executeTaskWithContext: After WithContext")
    }

    private fun coroutineCancellation() {
        Log.d(CoroutinePractice::class.simpleName, "coroutineCancellation: ")
        CoroutineScope(Dispatchers.IO).launch {
            executeTask()
        }
    }

    private suspend fun executeTask() {
        Log.d(CoroutinePractice::class.simpleName, "executeTask: ")
        val parentJob = CoroutineScope(Dispatchers.IO).launch {
            for(i in 0..1000) {
                if(isActive) {
                    executeLongRunningTask()
                    Log.d(CoroutinePractice::class.simpleName, "executeTask: $i")
                }
            }
        }
        delay(100)
        Log.d(CoroutinePractice::class.simpleName, "executeTask: Canceling parent job")
        parentJob.cancel()
        parentJob.join()
        Log.d(CoroutinePractice::class.simpleName, "executeTask: parent Completed..")
    }

    private fun executeLongRunningTask(){
        for(i in 0..100000000){

        }
    }

    private fun coroutineHierarchy() {
        Log.d(CoroutinePractice::class.simpleName, "coroutineHierarchy: ")
        CoroutineScope(Dispatchers.Main).launch {
            executeWork()
        }
    }

    private suspend fun executeWork() {
        Log.d(CoroutinePractice::class.simpleName, "executeWork: ")
        val parentJob = GlobalScope.launch(Dispatchers.Main){
            Log.d(CoroutinePractice::class.simpleName, "executeWork:Parent Started $coroutineContext")
            val childJob = launch(Dispatchers.IO) {
                Log.d(CoroutinePractice::class.simpleName, "executeWork:Child Started $coroutineContext")
                delay(3000)
                Log.d(CoroutinePractice::class.simpleName, "executeWork: Child Ended")
            }

            delay(2000)
            Log.d(CoroutinePractice::class.simpleName, "executeWork: Parent Ended")

        }
        delay(1000)
        // parent job and its child jobs are cancelled. ///
        parentJob.cancel()
        // parent Job wait for the Child Jobs to Complete and then its completed..//
        parentJob.join()
        Log.d(CoroutinePractice::class.simpleName, "executeWork: Parent Completed")
    }

    private fun asynAwaitExample1() {
        Log.d(CoroutinePractice::class.simpleName, "asynAwaitExample1: ")
        CoroutineScope(Dispatchers.Main).launch {
            // Async return the data type of last statement. //
            val job = CoroutineScope(Dispatchers.IO).async {
                Log.d(CoroutinePractice::class.simpleName, "asynAwaitExample1: Started")
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
            Log.d(CoroutinePractice::class.simpleName, "asynAwaitExample2: ${job1.await()}")
            Log.d(CoroutinePractice::class.simpleName, "printFollowers: ${job.await()} ")
        }

    }


    private fun coroutineBuilders() {
        Log.d(CoroutinePractice::class.simpleName, "coroutineBuilders: Start")
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(CoroutinePractice::class.simpleName, "coroutineBuilders: Print Followers")
            printFollowers()
        }
        Log.d(CoroutinePractice::class.simpleName, "coroutineBuilders: End")

    }

    suspend fun printFollowers() {
        Log.d(CoroutinePractice::class.simpleName, "printFollowers: ")
        var fbFollower = 0
        var instaFollower = 0
        val job = CoroutineScope(Dispatchers.IO).launch {
            fbFollower = getFollowers()
        }

        val job1 = CoroutineScope(Dispatchers.IO).launch {
            instaFollower = getInstaFollowers()
        }
        // join is used to wait for the coroutine to finish().
        // coroutine is remain in the suspended state.
        job.join()
        job1.join()
        Log.d(CoroutinePractice::class.simpleName, "instaFollower: $instaFollower ${job1.job}")
        Log.d(CoroutinePractice::class.simpleName, "fbFollower: $fbFollower ${job.job}")
        Log.d(CoroutinePractice::class.simpleName, "printFollowers: End")
    }

    suspend fun getFollowers():Int{
        Log.d(CoroutinePractice::class.simpleName, "getFollowers: ")
        delay(1000)
        return 100
    }
    suspend fun getInstaFollowers():Int{
        Log.d(CoroutinePractice::class.simpleName, "getInstaFollowers: ")
        delay(1500)
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


    fun dependentCoroutines() = runBlocking {
        val job1 = GlobalScope.launch(start = CoroutineStart.LAZY) {
            Log.d(CoroutinePractice::class.simpleName, "dependentCoroutines: job1 Started")
            delay(200)
            println("Pong")
            delay(200)
        }
        GlobalScope.launch {
            Log.d(CoroutinePractice::class.simpleName, "dependentCoroutines: job2 Started")
            delay(200)
            println("Ping")
            Log.d(CoroutinePractice::class.simpleName, "dependentCoroutines: job1 join")
            job1.join()
            println("Ping")
            delay(200)
        }
        Log.d(CoroutinePractice::class.simpleName, "dependentCoroutines: End")
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
        Log.d(CoroutinePractice::class.simpleName, "main: ")
        val job = launch {
            repeat(10){
                Log.d(CoroutinePractice::class.simpleName, "main: $it")
                delay(100)
            }
            Log.d(CoroutinePractice::class.simpleName, "main: Hello From Coroutine...")
        }
        Log.d(CoroutinePractice::class.simpleName, "main: delay")
        delay(600)
        Log.d(CoroutinePractice::class.simpleName, "main: cancel")
        job.cancel()
        Log.d(CoroutinePractice::class.simpleName, "main: join")
        job.join()
        Log.d(CoroutinePractice::class.simpleName, "main: End")
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