package com.chelz.sumullab8_9

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab81Binding
import java.util.Random

class Lab81Activity : AppCompatActivity() {

	lateinit var binding: ActivityLab81Binding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab81Binding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.button.setOnClickListener {
			val question = binding.editText.text.toString()
			if (question.isEmpty()) {
				Toast.makeText(this, "Enter the question", Toast.LENGTH_SHORT).show()
			} else {
				val random = Random()
				val value = random.nextBoolean()
				if (value) {
					Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show()
				} else
					Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show()
			}

		}
	}
}