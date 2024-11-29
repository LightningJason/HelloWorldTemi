package com.temi.helloworld.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject



interface MainViewModelInterface {
    abstract val numClicks: StateFlow<Int>
    abstract var locations: List<String>

    abstract fun onButtonClick(): Unit
    abstract fun onLocationButtonClick(location: String): Unit
}

@HiltViewModel
class MainViewModel @Inject constructor():
    ViewModel(),
    MainViewModelInterface,
    OnRobotReadyListener,
    OnGoToLocationStatusChangedListener
{
    private val _numClicks = MutableStateFlow(value = 0)
    override val numClicks = _numClicks.asStateFlow()

    val TAG = "MainViewModel"

    private val robot = Robot.getInstance()
    override lateinit var locations: List<String>

    // Location Status
    private val _locationReached = MutableStateFlow<String>(value = "")
    val locationReached = _locationReached.asStateFlow()

    init {
        robot.addOnRobotReadyListener(onRobotReadyListener = this)
        robot.addOnGoToLocationStatusChangedListener(listener = this)
    }

    override fun onButtonClick() {
        _numClicks.update { numClicks: Int ->
            val newNumClicks = numClicks + 1
            Log.i(TAG, "numClicks: $newNumClicks")
            Log.i(TAG, "locations: $locations")
            return@update newNumClicks
        }
    }

    override fun onLocationButtonClick(location: String) {
        Log.i(TAG, "location clicked: $location")
        robot.goTo(location)
    }

    /**
     * Called when connection with robot was established.
     *
     * @param isReady `true` when connection is open. `false` otherwise.
     */
    override fun onRobotReady(isReady: Boolean) {
        if (!isReady) return
        Log.i(TAG, "robot ready")

        locations = robot.locations
    }

    /**
     * Listen for status changes during 'go to location'.
     *
     *
     * Available statuses:
     *
     *  * [OnGoToLocationStatusChangedListener.START]
     *  * [OnGoToLocationStatusChangedListener.CALCULATING]
     *  * [OnGoToLocationStatusChangedListener.GOING]
     *  * [OnGoToLocationStatusChangedListener.COMPLETE]
     *  * [OnGoToLocationStatusChangedListener.ABORT]
     *  * [OnGoToLocationStatusChangedListener.REPOSING]
     *
     *
     * @param location Location of GoTo response.
     * @param status   Current status.
     */
    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        Log.i(TAG, "location: $location status: $status id: $descriptionId description: $description")
        if (status == "complete") {
            val ttsRequest: TtsRequest =
                TtsRequest.create(speech = "Hi, I have reached $location" , isShowOnConversationLayer = false)
            robot.speak(ttsRequest)
        }
    }
}

class MockMainViewModel(): MainViewModelInterface {
    private val _numClicks = MutableStateFlow(value = 0)
    override val numClicks = _numClicks.asStateFlow()
    override var locations: List<String> = listOf("New York", "Los Angeles", "Chicago", "Houston", "Phoenix")
    val TAG = "MockMainViewModel"

    override fun onButtonClick() {
        _numClicks.update { numClicks: Int ->
            val newNumClicks = numClicks + 1
            Log.i(TAG, "numClicks: $newNumClicks")
            return@update newNumClicks
        }
    }

    override fun onLocationButtonClick(location: String) {
        TODO("Not yet implemented")
    }
}
