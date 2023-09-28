package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

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
        
        //coroutineBuilders()
        //asynAwaitExample1()
        //asynAwaitExample2()
        //coroutineHierarchy()
        //coroutineCancellation()
        //withContextExample()

        //sequencePractice()
        //channelPractice()
        channelProducerConsumer()

//        simpleCoroutine()
//        main()
//        launchCoroutines()
//        dependentCoroutines()
//        jobHeirarchy()
//        asynAwaitExample()

    }

    private fun channelProducerConsumer() {
        Log.d(CoroutinePractice::class.simpleName, "channelProducerConsumer: ")
        val fruitArray = arrayOf("Apple", "Banana", "Pear", "Grapes",
            "Strawberry")
        fun produceFruits() = GlobalScope.produce<String> {
            for (fruit in fruitArray) {
                send(fruit)
                // Conditional close
                if (fruit == "Pear") {
                    // Signal that closure of channel
                    close()
                }
            }
        }
        runBlocking {
            val fruits = produceFruits()
            fruits.consumeEach { println(it) }
            println("Done!")
        }
    }

    private fun channelPractice() {
        Log.d(CoroutinePractice::class.simpleName, "channelPractice: ")
        val fruits = listOf("Apple","Orange","Grapes","Banana","Watermelon")
        val kotlinChannel = Channel<String>()

        runBlocking {
            GlobalScope.launch {
                for(fruit in fruits){
                    if(!kotlinChannel.isClosedForSend){
                        kotlinChannel.send(fruit)
                    }
                    if(fruit == "Banana"){
                        kotlinChannel.close()
                    }
                }
            }

            for (fruit in kotlinChannel){
                println(fruit)
            }
            println("Done")
        }

    }

    private fun sequencePractice() {
        Log.d(CoroutinePractice::class.simpleName, "sequencePractice: ")
            // 1
            val sequence = multipleValuesSequence()
            // 2
            sequence.forEach {
                println(it)
            }

    }

    // 3
    // it will print one value at a time.
    fun singleValueExample() = sequence {
        // 4
        println("Printing first value")
        // 5
        yield("Apple")
        // 6
        println("Printing second value")
        // 7
        yield("Orange")
        // 8
        println("Printing third value")
        // 9
        yield("Banana")
    }

    // it will print all values at a time.
    fun multipleValuesSequence() = sequence {
        // 4
        println("Printing All values.")
        yieldAll(1..10)
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
        Log.d(CoroutinePractice::class.simpleName, "printFollowers: $fbFollower ${job.job}")
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