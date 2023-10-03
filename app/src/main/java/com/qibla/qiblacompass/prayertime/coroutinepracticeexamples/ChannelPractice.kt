package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface Item {
    val name: String
    val color: String
}

data class Fruit(override val name: String, override val color:
String) : Item
data class Vegetable(override val name: String, override val
color: String) : Item

class ChannelPractice : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_practice)
        //sequencePractice()
        //channelPractice()
        //channelProducerConsumer()
        //bufferedChannels()
        //broadcastChannels()


        runBlocking {
            // 4
            val itemsChannel = produceItems()
            // 5
            val fruitsChannel = isFruit(itemsChannel)
            // 6
            val redChannel = isRed(fruitsChannel)
            // 7
            for (item in redChannel) {
                print("${item.name}, ")
            }
            // 8
            redChannel.cancel()
            fruitsChannel.cancel()
            itemsChannel.cancel()
            // 9
            println("Done!")
        }

    }

    private fun broadcastChannels() {
        Log.d(ChannelPractice::class.simpleName, "broadcastChannels: ")
        val fruitArray = arrayOf("Apple", "Banana", "Pear", "Grapes",
            "Strawberry")
        // 1
        val kotlinChannel = BroadcastChannel<String>(3)
        runBlocking {
            // 2
            kotlinChannel.apply {
                send(fruitArray[0])
                send(fruitArray[1])
                send(fruitArray[2])
            }
            //3 Consumers
            GlobalScope.launch {
                // 4
                kotlinChannel.openSubscription().let { channel ->
                    // 5
                    for (value in channel) {
                        println("Consumer 1: $value")
                    }
                    // 6
                }
            }

            GlobalScope.launch {
                kotlinChannel.openSubscription().let { channel ->
                    for (value in channel) {
                        println("Consumer 2: $value")
                    }
                }
            }
            // 7
            kotlinChannel.apply {
                send(fruitArray[3])
                send(fruitArray[4])
            }
            // 8
            println("Press a key to exit...")
            readLine()
            // 9
            kotlinChannel.close()
        }

    }

    private fun bufferedChannels() {
        Log.d(ChannelPractice::class.simpleName, "bufferedChannels: ")
        // Channel of capacity 2
        val kotlinBufferedChannel = Channel<String>(3)

        val fruitArray = arrayOf("Apple", "Banana", "Pear", "Grapes",
            "Strawberry")
        runBlocking {
            launch {
                for (fruit in fruitArray) {
                    kotlinBufferedChannel.send(fruit)
                    println("Produced: $fruit")
                }
                kotlinBufferedChannel.close()
            }
            launch {
                for (fruit in kotlinBufferedChannel) {
                    println("Consumed: $fruit")
                    delay(1000)
                }
            }
        }
    }

    // ------------ Pipeline ------------
    // 1
    fun produceItems() = GlobalScope.produce {
        val itemsArray = ArrayList<Item>()
        itemsArray.add(Fruit("Apple", "Red"))
        itemsArray.add(Vegetable("Zucchini", "Green"))
        itemsArray.add(Fruit("Grapes", "Green"))
        itemsArray.add(Vegetable("Radishes", "Red"))
        itemsArray.add(Fruit("Banana", "Yellow"))
        itemsArray.add(Fruit("Cherries", "Red"))
        itemsArray.add(Vegetable("Broccoli ", "Green"))
        itemsArray.add(Fruit("Strawberry", "Red"))
        // Send each item in the channel
        itemsArray.forEach {
            send(it)
        }
    }

    // 2
    fun isFruit(items: ReceiveChannel<Item>) = GlobalScope.produce{
        for (item in items) {
            // Send each item in the channel only if it is a fruit
            if (isFruit(item)) {
                send(item)
            }
        }
    }
    // 3
    fun isRed(items: ReceiveChannel<Item>) = GlobalScope.produce {
        for (item in items) {
            // Send each item in the channel only if it is red in color
            if (isRed(item)) {
                send(item)
            }
        }
    }

    // ------------ Helper Methods ------------//
    fun isFruit(item: Item): Boolean = item is Fruit
    fun isRed(item: Item): Boolean = (item.color == "Red")


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
}