package com.chelz.sumullab8_9

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab91Binding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.lang.Math.abs
import kotlin.random.Random

class Lab91Activity : AppCompatActivity() {

	lateinit var binding: ActivityLab91Binding

	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab91Binding.inflate(layoutInflater)
		setContentView(binding.root)
		binding.button.setOnClickListener {
			val count = ArrayList<Int>()
			val prob1 = binding.prob1.text.toString().toFloat()
			val prob2 = binding.prob2.text.toString().toFloat()
			val prob3 = binding.prob3.text.toString().toFloat()
			val prob4 = binding.prob4.text.toString().toFloat()
			val prob5 = 1 - prob1 - prob2 - prob3 - prob4

			val prob = arrayListOf(
				prob1,
				prob2,
				prob3,
				prob4,
				prob5,
			)
			val number = binding.trial.text.toString().toInt()

			for (i in 1..number) {

				val random = Random.nextFloat()
				var p = prob[0]
				var j = 0
				while (p < random) {
					j++
					p += prob[j]
				}
				count.add(j)
			}

			var mean = 0.0
			var empMean = 0.0
			val empProb = arrayListOf(
				count.count { it == 0 } / count.size.toFloat(),
				count.count { it == 1 } / count.size.toFloat(),
				count.count { it == 2 } / count.size.toFloat(),
				count.count { it == 3 } / count.size.toFloat(),
				count.count { it == 4 } / count.size.toFloat(),
			)

			for (i in 0..4) {
				mean += (i + 1) * prob[i]
				empMean += (i + 1) * empProb[i]
			}

			binding.average.text = "average $mean (error ${abs(empMean - mean) / abs(mean)})"

			var variance = 0.0
			var empVariance = 0.0
			for (i in 0..4) {
				variance += (i + 1) * (i + 1) * prob[i]
				empVariance += (i + 1) * (i + 1) * empProb[i]
			}
			variance -= mean * mean
			empVariance -= empMean * empMean
			binding.variance.text = "variance $variance (error ${abs(empVariance - variance) / abs(variance)})"

			var chiSqr = 0.0
			for (i in 0..4) {
				chiSqr += count.count { it == i } * count.count { it == i } / (count.size * prob[i])
			}
			chiSqr -= count.size

			binding.chi.text = "chi $chiSqr > 9.488 ${chiSqr > 9.488}"
			val title = "Title"

			val entries: ArrayList<BarEntry> = ArrayList()

			entries.add(BarEntry(1f, empProb[0]))
			entries.add(BarEntry(2f, empProb[1]))
			entries.add(BarEntry(3f, empProb[2]))
			entries.add(BarEntry(4f, empProb[3]))
			entries.add(BarEntry(5f, empProb[4]))
			val barDataSet = BarDataSet(entries, title)

			val data = BarData(barDataSet)
			binding.chart.data = data
			binding.chart.invalidate()
		}
	}
}