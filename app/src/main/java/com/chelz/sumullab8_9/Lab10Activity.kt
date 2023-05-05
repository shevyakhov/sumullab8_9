package com.chelz.sumullab8_9

import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab10Binding
import kotlin.math.ln
import kotlin.random.Random

class Lab10Activity : AppCompatActivity() {

	lateinit var binding: ActivityLab10Binding

	class Team(val id: Int, val name: String, var points: Int, var goals: Int, var fool: Int, val priority: Double, var played: Int)

	val eplTeams = arrayOf(
		"Chelsea",
		"Manchester City",
		"Liverpool",
		"Manchester United",
		"Arsenal",
		"Tottenham Hotspur",
		"Leicester City",
		"Everton",
		"West Ham United",
		"Leeds United",
		"Aston Villa",
		"Wolverhampton Wanderers",
		"Crystal Palace",
		"Southampton",
		"Newcastle United",
		"Brighton & Hove Albion",
		"Burnley",
		"Fulham",
		"West Bromwich Albion",
		"Sheffield United"
	)
	val teamsPriority = arrayListOf(20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab10Binding.inflate(layoutInflater)
		setContentView(binding.root)
		val teams = arrayListOf<Team>()
		for (i in eplTeams.indices) {
			val team = Team(i, eplTeams[i], 0, 0, 0, teamsPriority[i].toDouble(), 0)
			teams.add(team)
		}

		for (i in teams.indices) {

			val team = teams[i]
			repeat(2) {
				val matches = teams.filter { it.id > i }.shuffled()
				if (matches.isNotEmpty()) {
					for (opponent in matches) {
						val tPrior = team.priority / (team.priority + opponent.priority) * 2.5
						val oPrior = opponent.priority / (team.priority + opponent.priority) * 2
						var myBall = 0
						var oppBall = 0
						var s = 0.0
						var k = 0.0

						while (true) {
							s += ln(Random.nextDouble())
							if (s < -tPrior) {
								break
							} else
								myBall++
						}
						while (true) {
							k += ln(Random.nextDouble())
							if (k < -oPrior) {
								break
							} else
								oppBall++
						}
						when {
							myBall == oppBall -> {
								teams[i].goals += myBall
								teams[i].points += 1
								teams[i].played += 1
								teams[i].fool += oppBall
								val opp = teams.indexOfFirst { it.id == opponent.id }
								teams[opp].goals += oppBall
								teams[opp].fool += myBall
								teams[opp].points += 1
								teams[opp].played += 1

							}

							myBall > oppBall  -> {
								teams[i].goals += myBall
								teams[i].points += 3
								teams[i].played += 1
								teams[i].fool += oppBall

								val opp = teams.indexOfFirst { it.id == opponent.id }
								teams[opp].goals += oppBall
								teams[opp].played += 1
								teams[opp].fool += myBall

							}

							myBall < oppBall  -> {
								teams[i].goals += myBall
								teams[i].played += 1
								teams[i].fool += oppBall

								val opp = teams.indexOfFirst { it.id == opponent.id }
								teams[opp].goals += oppBall
								teams[opp].points += 3
								teams[opp].played += 1
								teams[i].fool += oppBall

							}
						}

					}


				}
			}

		}
		for (i in teams.sortedWith(compareBy({ it.points }, { it.goals })).reversed()) {
			val row = TableRow(this)
			val name = TextView(this).apply {
				text = i.name
			}
			val points = TextView(this).apply {
				text = i.points.toString()
			}
			val goals = TextView(this).apply {
				text = i.goals.toString()
			}
			val played = TextView(this).apply {
				text = i.played.toString()
			}
			val concieved = TextView(this).apply {
				text = i.fool.toString()
			}
			row.addView(name)
			row.addView(points)
			row.addView(goals)
			row.addView(concieved)
			row.addView(played)
			binding.table.addView(row)
		}

	}
}