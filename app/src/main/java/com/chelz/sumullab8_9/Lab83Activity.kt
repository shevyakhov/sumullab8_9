package com.chelz.sumullab8_9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab83Binding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlin.random.Random

class Lab83Activity : AppCompatActivity() {

	lateinit var binding: ActivityLab83Binding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab83Binding.inflate(layoutInflater)
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

			val title = "Title"

			val entries: ArrayList<BarEntry> = ArrayList()

			entries.add(BarEntry(0f, count.count { it == 0 }.toFloat()))
			entries.add(BarEntry(1f, count.count { it == 1 }.toFloat()))
			entries.add(BarEntry(2f, count.count { it == 2 }.toFloat()))
			entries.add(BarEntry(3f, count.count { it == 3 }.toFloat()))
			entries.add(BarEntry(4f, count.count { it == 4 }.toFloat()))

			val barDataSet = BarDataSet(entries, title)

			val data = BarData(barDataSet)
			binding.chart.data = data
			binding.chart.invalidate()
		}


	}
}