package com.chelz.sumullab8_9

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.lab81.setOnClickListener {
			intent = Intent(this, Lab81Activity::class.java)
			startActivity(intent)
		}
		binding.lab82.setOnClickListener {
			intent = Intent(this, Lab82Activity::class.java)
			startActivity(intent)
		}
		binding.lab83.setOnClickListener {
			intent = Intent(this, Lab83Activity::class.java)
			startActivity(intent)
		}
		binding.lab91.setOnClickListener {
			intent = Intent(this, Lab91Activity::class.java)
			startActivity(intent)
		}
		binding.lab10.setOnClickListener {
			intent = Intent(this, Lab10Activity::class.java)
			startActivity(intent)
		}
		binding.lab11.setOnClickListener {
			intent = Intent(this, Lab11Activity::class.java)
			startActivity(intent)
		}
		binding.lab12.setOnClickListener {
			intent = Intent(this, Lab12Activity::class.java)
			startActivity(intent)
		}
		binding.lab13.setOnClickListener {
			intent = Intent(this, Lab13Activity::class.java)
			startActivity(intent)
		}
		binding.lab14.setOnClickListener {
			intent = Intent(this, Lab14Activity::class.java)
			startActivity(intent)
		}
	}
}