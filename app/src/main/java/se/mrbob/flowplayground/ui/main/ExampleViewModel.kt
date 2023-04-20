@file:OptIn(DelicateCoroutinesApi::class)

package se.mrbob.flowplayground.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

sealed class GameState {
    object Starting : GameState()
    data class Turn(val name: String, val signal: CompletableDeferred<Int>) : GameState()
    data class Finished(val winner:String) : GameState()
}

class ExampleViewModel : ViewModel() {

    private val gameValue = 4
    fun game(player1: String, player2: String) = flow {
        emit(GameState.Starting)
        var current = randomStarter(player1, player2)
        while(currentCoroutineContext().isActive) {
            val guess = CompletableDeferred<Int>()
            emit(GameState.Turn(current, guess))
            val guessValue = guess.await()
            current = when {
                guessValue == gameValue -> {
                    emit(GameState.Finished(current))
                    return@flow
                }
                current == player1 -> player2
                else -> player1
            }
        }
    }

    private fun randomStarter(one: String, two: String): String = when (Random.nextBoolean()) {
        true -> one
        false -> two
    }
}