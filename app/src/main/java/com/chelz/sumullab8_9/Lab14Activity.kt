package com.chelz.sumullab8_9

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab14Binding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*
import kotlin.math.ln

class Lab14Activity : AppCompatActivity() {

	private lateinit var binding: ActivityLab14Binding

	private lateinit var Bank: BankModel
	private var N: Int = 0
	private var T: Double = 0.0
	private var NumberOfOperators: Int = 0
	private var flowLambda: Double = 0.0
	private var serversLambda: Double = 0.0
	private lateinit var EmpiricStat: HashMap<Int, Double>
	private lateinit var TheoreticalStat: HashMap<Int, Double>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab14Binding.inflate(layoutInflater)
		setContentView(binding.root)

		var xAxis: XAxis
		run {
			xAxis = binding.chart1.xAxis
			xAxis.mAxisMinimum = 0f
			xAxis.enableGridDashedLine(10f, 10f, 0f)
		}
		binding.Start.setOnClickListener {
			flowLambda = binding.fl.text.toString().toDouble()
			serversLambda = binding.sl.text.toString().toDouble()
			NumberOfOperators = binding.OpBox.text.toString().toInt()
			Bank = BankModel(flowLambda, serversLambda, NumberOfOperators)

			T = binding.TBox.text.toString().toDouble()
			N = binding.Nbox.text.toString().toInt()

			for (i in 0 until N) {
				Bank.PredetermineFlow(T)
				Bank.Work(T)
				Bank.addStat()
			}
			EmpiricStat = Bank.getStat(N)
			TheoreticalStat = GetTheoryStat(EmpiricStat)

			binding.AllCustomersBox.text = "Total customers: " + (Bank.CustomerAll / N).toString()
			binding.ServedBox.text = "Total served: " + (Bank.CustomerServed / N).toString()
			binding.AvgInBankBox.text = "Avg. time in bank: " + String.format("%.2f", Bank.getAvgTimeInBank())
			binding.AvgInQBox.text = "Avg. time in queue: " + String.format("%.2f", Bank.getAvgTimeInQ())

			val ds1 = LineDataSet(mutableListOf(Entry(0F, 0f)), "customer").apply {
				circleRadius = 1f
				lineWidth = 3F
			}
			val ds2 = LineDataSet(mutableListOf(Entry(0F, 0f)), "customer").apply {
				circleRadius = 1f
				color = Color.RED
				lineWidth = 3F
			}
			binding.chart1.data = LineData(ds1, ds2)

			for (i in EmpiricStat.keys) {
				binding.chart1.data.dataSets[0].addEntry(Entry(i.toFloat(), EmpiricStat[i]?.toFloat() ?: 0f))
			}
			for (i in TheoreticalStat.keys) {
				binding.chart1.data.dataSets[1].addEntry(Entry(i.toFloat(), TheoreticalStat[i]?.toFloat() ?: 0f))
			}
			binding.chart1.invalidate()
		}
	}

	private fun GetTheoryStat(stat: HashMap<Int, Double>): HashMap<Int, Double> {
		val rho = flowLambda / serversLambda
		val dist = StationaryDistributionOfClients()
		for (i in stat.keys.toList()) {
			stat[i] = if (i < NumberOfOperators) {
				(Math.pow(rho, i.toDouble()) / Factorial(i)) * dist
			} else {
				(Math.pow(rho, i.toDouble()) / (Factorial(i) * Math.pow(NumberOfOperators.toDouble(), (i - NumberOfOperators).toDouble()))) * dist
			}
		}
		return stat
	}

	private fun StationaryDistributionOfClients(): Double {
		var temp1 = 0.0
		val rho = flowLambda / serversLambda
		for (i in 0 until NumberOfOperators) {
			temp1 += Math.pow(rho, i.toDouble()) / Factorial(i)
		}
		val temp2 = Math.pow(rho, (NumberOfOperators + 1).toDouble()) / Factorial(NumberOfOperators) * (NumberOfOperators - rho)
		val value = Math.pow(temp1 + temp2, -1.0)
		return value
	}

	private fun Factorial(k: Int): Int {
		var ans = 1
		for (i in 1..k) {
			ans *= i
		}
		return ans
	}
}

class BankModel(private val fl: Double, private val sl: Double, private val NumOper: Int) {

	private var Time: Double = 0.0
	private lateinit var Events: SortedMap<Double, String>
	val queue: Queue = Queue()
	val servers: ServiceArea = ServiceArea()
	val flow: InputFlow = InputFlow(fl)
	var CustomerAll: Int = 0
	var CustomerServed: Int = 0
	val EmpiricStat: HashMap<Int, Double> = HashMap()
	val EmpiricAvgTimeInBank: ArrayList<Double> = ArrayList()
	val EmpiricAvgTimeInQ: ArrayList<Double> = ArrayList()

	init {
		Time = 0.0
		CustomerAll = 0
		CustomerServed = 0

		for (i in 0 until NumOper) {
			servers.add(Operator(sl))
		}
	}

	fun Work(T: Double) {
		Time = 0.0
		var evnt: Map.Entry<Double, String>
		while (Time < T) {
			Events.entries.sortedBy { it.key }
			evnt = Events.entries.first()
			if (evnt.value == "flow") {
				CustomerAll++
				val c: Customer = flow.processEvent(Time)
				val o: Operator? = servers.getFreeOperator()
				if (o != null) {
					val busy: Int = servers.howManyBusy()
					val temp: Map.Entry<Double, String> = o.getNextEvent(Time, c)
					Events[temp.key] = temp.value
				} else {
					queue.add(c, Time)
				}
				Events.remove(evnt.key)
				Time = evnt.key
			} else if (evnt.value == "oper") {
				val o: Operator = servers.getOperator(evnt.key)!!
				o.processEvent(Time, EmpiricAvgTimeInBank, EmpiricAvgTimeInQ)
				Time = evnt.key
				Events.remove(evnt.key)
				CustomerServed++
				if (queue.customers.isNotEmpty()) {
					val c: Customer = queue.getNextCustomer(Time)
					val busy: Int = servers.howManyBusy()
					val temp: Map.Entry<Double, String> = o.getNextEvent(Time, c)
					Events[temp.key] = temp.value
				}
			}
		}
	}

	fun PredetermineFlow(T: Double) {
		Time = 0.0
		Events = TreeMap()
		servers.allOpsFree()
		queue.clearQueue()
		queue.setSize(0)

		var evnt: Map.Entry<Double, String>
		while (Time < T) {
			evnt = flow.getNextEvent(Time)
			if (evnt.key > 0) {
				Events[evnt.key] = evnt.value
				Time = evnt.key
			}
		}
		Time = 0.0
	}

	fun addStat() {
		val state = queue.getSize() + servers.howManyBusy()
		EmpiricStat[state] = EmpiricStat[state]?.plus(1) ?: 1.0
	}

	fun getStat(N: Int): HashMap<Int, Double> {
		val statistic = HashMap(EmpiricStat)
		for (i in EmpiricStat.keys) {
			statistic[i] = EmpiricStat[i]?.div(N) ?: 0.0
		}
		return statistic
	}

	fun getAvgTimeInBank(): Double {
		var avg = 0.0
		for (value in EmpiricAvgTimeInBank) {
			avg += value
		}
		avg /= EmpiricAvgTimeInBank.size
		return avg
	}

	fun getAvgTimeInQ(): Double {
		var avg = 0.0
		for (value in EmpiricAvgTimeInQ) {
			avg += value
		}
		avg /= EmpiricAvgTimeInQ.size
		return avg
	}

	inner class Queue {

		val customers: MutableList<Customer> = ArrayList()
		var size: Int = 0

		fun add(c: Customer, T: Double) {
			size++
			c.timeInQ = T
			customers.add(c)
		}

		fun getNextCustomer(T: Double): Customer {
			size--
			val cus: Customer = customers.first()
			customers.remove(cus)
			if (cus.timeInQ != 0.0) {
				cus.timeInQ = T - cus.timeInQ
			}
			return cus
		}

		fun isEmpty(): Boolean {
			return customers.isEmpty()
		}

		fun clearQueue() {
			customers.clear()
		}

		@JvmName("setSize1")
		fun setSize(x: Int) {
			size = x
		}

		@JvmName("getSize1")
		fun getSize(): Int {
			return size
		}
	}

	inner class Customer(T: Double) {

		var timeInBank: Double = 0.0
		val startingTime: Double = T
		var timeInQ: Double = 0.0
	}

	inner class Operator(sl: Double) {

		private val rnd = kotlin.random.Random
		private val states: Array<String> = arrayOf("Free", "Busy")
		private var state: String = states[0]
		var endTime: Double = 0.0
		private val lambda: Double = sl
		lateinit var cus: Customer

		fun isFree(): Boolean {
			return state == states[0]
		}

		fun getNextEvent(T: Double, c: Customer): Map.Entry<Double, String> {
			endTime = T + (-ln(rnd.nextDouble()) / lambda)
			cus = c
			state = states[1]
			return AbstractMap.SimpleEntry(endTime, "oper")
		}

		fun processEvent(T: Double, EATIB: ArrayList<Double>, EATIQ: ArrayList<Double>) {
			cus.timeInBank = (T - cus.startingTime) + cus.timeInQ
			EATIB.add(cus.timeInBank)
			EATIQ.add(cus.timeInQ)
			state = states[0]
		}

		fun free() {
			state = states[0]
		}
	}

	inner class InputFlow(l: Double) {

		private var NextArrival: Double = 0.0
		private val rnd = kotlin.random.Random
		private val lambda: Double = l

		fun getNextEvent(T: Double): Map.Entry<Double, String> {
			NextArrival = T + (-Math.log(rnd.nextDouble()) / lambda)
			return AbstractMap.SimpleEntry(NextArrival, "flow")
		}

		fun processEvent(T: Double): Customer {
			return Customer(T)
		}
	}

	inner class ServiceArea {

		val operators: MutableList<Operator> = ArrayList()

		fun getFreeOperator(): Operator? {
			for (oper in operators) {
				if (oper.isFree()) {
					return oper
				}
			}
			return null
		}

		fun add(o: Operator) {
			operators.add(o)
		}

		fun getOperator(t: Double): Operator? {
			return operators.firstOrNull { it.endTime == t }
		}

		fun allOpsFree() {
			for (oper in operators) {
				oper.free()
			}
		}

		fun howManyBusy(): Int {
			var count = operators.size
			for (oper in operators) {
				if (oper.isFree()) {
					count--
				}
			}
			return count
		}
	}

}




