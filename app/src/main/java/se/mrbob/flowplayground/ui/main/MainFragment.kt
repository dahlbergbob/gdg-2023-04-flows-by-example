package se.mrbob.flowplayground.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import se.mrbob.flowplayground.R
import se.mrbob.flowplayground.threadName
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

@SuppressLint("SetTextI18n")
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private val exampleViewModel: ExampleViewModel by viewModels()
    private lateinit var message1: TextView
    private lateinit var message2: TextView
    private lateinit var message3: TextView
    private lateinit var message4: TextView
    private lateinit var message5: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        message1 = view.findViewById(R.id.message1)
        message2 = view.findViewById(R.id.message2)
        message3 = view.findViewById(R.id.message3)
        message4 = view.findViewById(R.id.message4)
        message5 = view.findViewById(R.id.message5)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        simpleCollect()
        //coldFlows()
        //oneFlowToRuleThemAll()
        //proveSameFlow()
        //proveSameFlow2()
        //collectingSmarter()
        //collectingDispatcher()
        //collectingControlledDispatcher()
        //collectingChangingDispatcher()
        //game()
        //killGame()
    }


    private fun simpleCollect() {
        viewModel.viewModelScope.launch {
            viewModel.countDown().collect {
                message1.text = "$it"
            }
        }
    }

    private fun coldFlows() {
        viewModel.viewModelScope.launch {
            viewModel.countDown().onEach {
                message1.text = "$it"
            }.collect()
        }
    }

    private fun oneFlowToRuleThemAll() {
        viewModel.viewModelScope.launch {
            viewModel.countDownFlow.collect {
                message1.text = "$it"
            }
            viewModel.countDownFlow.collect {
                message2.text = "$it"
            }
        }
    }

    private fun proveSameFlow() {
        viewModel.viewModelScope.launch {
            val flow1 = viewModel.countDownFlowWithID
            val flow2 = viewModel.countDownFlowWithID
            flow1.collect {
                message1.text = it
            }
            flow2.collect {
                message2.text = it
            }
            message3.text = "Same ? ${flow1 === flow2}"
        }
    }

    private fun proveSameFlow2() {
        viewModel.viewModelScope.launch {
            val flow1 = viewModel.countDownFlowWithRealID
            val flow2 = viewModel.countDownFlowWithRealID
            flow1.collect {
                message1.text = it
            }
            flow2.collect {
                message2.text = it
            }
            message3.text = "Same ? ${flow1 === flow2}"
        }
    }

    private fun collectingSmarter() {
            viewModel.countDown()
                .onEach { message1.text = "$it" }
                .launchIn(viewModel.viewModelScope)

            viewModel.countDown()
                .onEach { message2.text = "$it" }
                .launchIn(viewModel.viewModelScope)
    }

    private fun collectingDispatcher() {
        viewModel.countDown()
            .onEach { message1.text = "$it $threadName" }
            .launchIn(viewModel.viewModelScope)
        viewModel.countDown()
            .onEach { message2.text = "$it $threadName" }
            .launchIn(viewModel.viewModelScope + Dispatchers.IO)
    }

    private fun collectingControlledDispatcher() {
        viewModel.countDownOnIO()
            .onEach { message1.text = "$it \ncollect on $threadName" }
            .launchIn(viewModel.viewModelScope)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun collectingChangingDispatcher() {
        viewModel.countDownOnSingle()
            .map { "$it \n$threadName" }
            .flowOn(Dispatchers.IO)
            .map { "$it \n$threadName" }
            .flowOn(Dispatchers.Default)
            .onEach { message1.text = "$it \ncollect on $threadName" }
            .launchIn(viewModel.viewModelScope)
    }

    private fun game() {
        exampleViewModel.game("Bob", "Erik")
            .onEach {
                when(it) {
                    is GameState.Starting -> message1.text = "Starting game"
                    is GameState.Finished -> message3.text = "Winner is ${it.winner}"
                    is GameState.Turn -> {
                        message2.text = "Now playing ${it.name}"
                        delay(Random.nextLong(1500L..2000L))
                        val guess = Random.nextInt(1..9)
                        message2.text = "${message2.text} ($guess)"
                        delay(500)
                        it.signal.complete(guess)
                    }
                }
            }
            .onCompletion {
                message5.text = "Completed"
            }
            .launchIn(exampleViewModel.viewModelScope)
    }

    private fun killGame() {
        exampleViewModel.viewModelScope.launch {
            delay(3000)
            exampleViewModel.viewModelScope.coroutineContext.cancelChildren()
        }
    }
}