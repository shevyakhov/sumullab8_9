package com.chelz.sumullab8_9

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab82Binding
import com.chelz.sumullab8_9.databinding.ActivityLab91Binding
import com.chelz.sumullab8_9.databinding.ActivityMainBinding

class Lab91Activity : AppCompatActivity() {

	lateinit var binding: ActivityLab91Binding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab91Binding.inflate(layoutInflater)
		setContentView(binding.root)

	}
}