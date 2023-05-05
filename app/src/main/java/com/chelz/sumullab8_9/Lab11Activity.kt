package com.chelz.sumullab8_9

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab11Binding
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.nextUp
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random

class Lab11Activity : AppCompatActivity() {

	private lateinit var binding: ActivityLab11Binding

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab11Binding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.button.setOnClickListener {
			val mean = binding.mean.text.toString().toDouble()
			val variance = binding.variance.text.toString().toDouble()
			val sample = binding.sample.text.toString().toInt()

			val nrv = DoubleArray(sample) { ciForAll(mean, variance) }

			val numIntervals = round(sqrt(sample.toDouble())).toInt()
			val minVal = nrv.minOrNull()!!
			val maxVal = nrv.maxOrNull()!!
			val intervalSize = (maxVal - minVal) / numIntervals

			val intervals = IntArray(numIntervals)
			for (x in nrv) {
				val index = ((x - minVal) / intervalSize).toInt()
				if (index < 0) {
					intervals[0]++
				} else if (index >= numIntervals) {
					intervals[numIntervals - 1]++
				} else {
					intervals[index]++
				}
			}

			val entries = ArrayList<BarEntry>()
			for (i in 0 until numIntervals) {
				val intervalStart = minVal + i * intervalSize
				val intervalEnd = minVal + (i + 1) * intervalSize
				entries.add(BarEntry(i.toFloat(), intervals[i].toFloat() / sample))
			}
			val data = CombinedData()

			data.setData(BarData(BarDataSet(entries, "bar").apply {
				color = Color.rgb(60, 220, 78);
				valueTextColor = Color.rgb(60, 220, 78);
				valueTextSize = 10f;
				axisDependency = YAxis.AxisDependency.LEFT;
			}))
			data.setData(LineData(LineDataSet(entries as List<Entry>?, "line").apply {
				color = Color.rgb(240, 70, 70);
				lineWidth = 2.5f;
				setCircleColor(Color.rgb(240, 238, 70));
				circleRadius = 5f;
				fillColor = Color.rgb(240, 238, 70);
				mode = LineDataSet.Mode.CUBIC_BEZIER;
				setDrawValues(true);
				valueTextSize = 10f;
				valueTextColor = Color.rgb(240, 238, 70);
			}))

			var empMean = 0.0
			val empProb = ArrayList<Double>()
			for (i in 0 until numIntervals) {
				empProb.add(intervals[i].toDouble() / sample)
			}

			for (i in 0 until numIntervals) {
				empMean += i * empProb[i]
			}

			binding.averagetext.text = "average $mean (error ${abs(empMean - mean) / abs(mean)})"

			var empVariance = 0.0
			for (i in 0 until numIntervals) {
				empVariance += (i + 1) * (i + 1) * empProb[i]
			}
			empVariance -= empMean * empMean
			binding.variancetext.text = "variance $variance (error ${abs(empVariance - variance) / abs(variance)})"

			var chiSqr = 0.0
			for (i in 0 until numIntervals) {
				chiSqr += (intervals[i] * intervals[i].toFloat()) / (sample * (intervals[i].toFloat() / sample))
			}
			chiSqr -= sample

			binding.chitext.text = "chi $chiSqr > 9.488 ${chiSqr > 9.488}"

			binding.chart.data = data
			binding.chart.invalidate()
		}

	}

	private fun prob(i: Int, mean: Double, variance: Double): Double {
		return 1 / (sqrt(variance) * sqrt(2 * PI)) * exp(-(i - mean) * (i - mean) / (2 * variance * variance))
	}

	fun sensorBase(low: Double = 0.0, high: Double = 1.0, size: Int? = null): DoubleArray {
		return DoubleArray(size ?: 1) { Random.nextDouble(low.nextUp(), high) }
	}

	fun ci(): Double {
		var sum = 0.0
		for (i in 1..12) {
			sum += sensorBase().first()
		}
		return sum - 6
	}

	fun ciForAll(a: Double, sigma: Double): Double {
		return a + sqrt(sigma) * ci()
	}


}