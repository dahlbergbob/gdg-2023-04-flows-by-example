@file:OptIn(DelicateCoroutinesApi::class)

package se.mrbob.flowplayground.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import se.mrbob.flowplayground.threadName
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor

@OptIn(DelicateCoroutinesApi::class)
class MainViewModel : ViewModel() {

    fun countDown() = flowOf(3, 2, 1, 0 ).onEach {
        delay(1000)
    }

    val countDownFlow = flowOf(3, 2, 1, 0 ).onEach {
        delay(1000)
    }

    private fun shortId() = UUID.randomUUID().toString().take(6)
    val countDownFlowWithID: Flow<String> = flow {
        val id = shortId()
        for (i in 3 downTo 0) {
            emit("$id : $i")
            delay(1000)
        }
    }

    val countDownFlowWithRealID: Flow<String> = MyFlow(shortId()) { id ->
        for (i in 3 downTo 0) {
            emit("$id : $i")
            delay(1000)
        }
    }
    @OptIn(FlowPreview::class)
    class MyFlow<T>(private val id: String, val block: suspend FlowCollector<T>.(String) -> Unit) : AbstractFlow<T>() {
        override suspend fun collectSafely(collector: FlowCollector<T>) {
            collector.block(id)
        }
    }

    fun countDownOnIO() = flowOf(3, 2, 1, 0 )
        .onEach { delay(1000) }
        .map { "$it :: $threadName" }
        .flowOn(Dispatchers.IO)


    fun countDownOnSingle() = flowOf(3, 2, 1, 0 )
        .onEach { delay(1000) }
        .map { "$it :: $threadName" }
        .flowOn(newSingleThreadContext("my thread"))
}