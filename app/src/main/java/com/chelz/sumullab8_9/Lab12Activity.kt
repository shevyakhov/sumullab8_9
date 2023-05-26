package com.chelz.sumullab8_9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.Random.Companion.continuousMarkovProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.log
import java.util.Calendar
import java.util.Date
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class Lab12Activity : AppCompatActivity() {

	private lateinit var currentTime: Date
	private var currentWeather: Int = 0
	private var frequencies: MutableList<Int> = MutableList(5) { 0 }
	private var weatherChanges: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val Q = arrayOf(
			doubleArrayOf(-0.05, 0.03, 0.015, 0.0025, 0.0025),
			doubleArrayOf(0.08, -0.2, 0.08, 0.02, 0.02),
			doubleArrayOf(0.04, 0.04, -0.1, 0.01, 0.01),
			doubleArrayOf(0.15, 0.1, 0.1, -0.4, 0.05),
			doubleArrayOf(0.05, 0.1, 0.04, 0.01, -0.2)
		)
		setContentView(R.layout.activity_lab12)
		val initTime = Date()
		currentTime = initTime

		val simulateWeather = weatherSimulation(Q, 0, initTime)
		val interval = 200L

		CoroutineScope(Dispatchers.IO).launch {
			while (true) {
				val (newTime, newWeather) = simulateWeather.invoke()
				currentTime = Date(newTime.time)
				currentWeather = newWeather

				frequencies = frequencies.mapIndexed { index, value ->
					if (index == newWeather) value + 1 else value
				}.toMutableList()
				weatherChanges++

				updateUI()

				delay(interval)
			}
		}



		weatherSimulation(Q, 1, Calendar.getInstance().time)
	}

	private fun updateUI() {
		runOnUiThread {

			println(currentTime)
			println(currentWeather)
			println(frequencies)

		}
	}


}

data class WeatherSimulationStep(val time: Date, val weather: Int)

fun incrementTime(date: Date, increment: Double) {
	var incrementTime = increment.roundToInt()
	incrementTime = if (incrementTime >= 1) incrementTime else 1

	date.hours += incrementTime
}

fun weatherSimulation(Q: Array<DoubleArray>, current: Int, initTime: Date): () -> WeatherSimulationStep {
	var currentWeather = current
	val currentTime = Date(initTime.time)
	val nextTime = Date(initTime.time)

	val (time, nextEvent) = continuousMarkovProcess(Q, currentWeather)
	incrementTime(nextTime, time)
	var nextWeather = nextEvent

	return {
		incrementTime(currentTime, 1.0)

		if (currentTime.time < nextTime.time) {
			WeatherSimulationStep(currentTime, currentWeather)
		} else {
			val ret = WeatherSimulationStep(currentTime, nextWeather)

			val (time, nextEvent) = continuousMarkovProcess(Q, currentWeather)
			incrementTime(nextTime, time)
			nextWeather = nextEvent
			currentWeather = nextEvent

			ret
		}
	}
}

data class MarkovProcessReturn(val time: Double, val nextEvent: Int)

class Random {
	companion object {

		private fun generate() = Random.nextDouble()

		fun getBoolean(probability: Double = 0.5) = Random.nextDouble() < probability

		private fun getFromMultipleEvents(multipleEvents: MultipleEvents): Int {
			var A = generate()
			var i = -1

			val probabilitiesArray = (multipleEvents as MultipleEvents.WithProbabilities).probabilitiesArray
			probabilitiesArray.size

			do {
				A -= probabilitiesArray[++i]
			} while (A > 0)

			return i
		}

		fun normalDistribution(mean: Double, variance: Double): Double {
			val base = sqrt(-2.0 * ln(generate())) * sin(2.0 * PI * generate())
			return base * sqrt(variance) + mean
		}

		fun continuousMarkovProcess(Q: Array<DoubleArray>, current: Int): MarkovProcessReturn {
			val currentQ = Q[current][current]
			val time = log(generate()) / currentQ

			val probabilitiesArray = Q[current].map { value -> -value / currentQ }.toDoubleArray()
			probabilitiesArray[current] = 0.0
			val nextEvent = getFromMultipleEvents(MultipleEvents.WithProbabilities(probabilitiesArray))

			return MarkovProcessReturn(time, nextEvent)
		}
	}
}

sealed class MultipleEvents {
	data class WithProbabilities(val probabilitiesArray: DoubleArray) : MultipleEvents()
	data class WithArraySize(val arraySize: Int) : MultipleEvents()
}
