package com.chelz.sumullab8_9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab82Binding
import kotlin.random.Random

class Lab82Activity : AppCompatActivity() {

	val text = arrayListOf(
		"It is certain (Бесспорно)",
		"It is decidedly so (Предрешено)",
		"Without a doubt (Никаких сомнений)",
		"Yes — definitely (Определённо да)",
		"You may rely on it (Можешь быть уверен в этом)",
		"As I see it",
		"Most likely (Вероятнее всего)",
		"Outlook good (Хорошие перспективы)",
		"Signs point to yes (Знаки говорят — «да»)",
		"Yes (Да)",
		"Reply hazy",
		"try again",
		"Ask again later (Спроси позже)",
		"Better not tell you now (Лучше не рассказывать)",
		"Cannot predict now (Сейчас нельзя предсказать)",
		"Concentrate and ask again (Сконцентрируйся и спроси опять)",
		"Don’t count on it (Даже не думай)",
		"My reply is no (Мой ответ — «нет»)",
		"My sources say no (По моим данным — «нет»)",
		"Outlook not so good (Перспективы не очень хорошие)",
		"Very doubtful (Весьма сомнительно)"
	)
	lateinit var binding: ActivityLab82Binding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab82Binding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.button.setOnClickListener {
			val random = Random.nextInt(0, text.size - 1)
			binding.button.text = text[random]
		}
	}
}