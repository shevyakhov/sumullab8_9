package com.chelz.sumullab8_9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chelz.sumullab8_9.databinding.ActivityLab14Binding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.LinkedList

class Lab14Activity : AppCompatActivity() {

	private lateinit var binding: ActivityLab14Binding
	private var isStarted = false
	private var job: Job = Job()
	var busyOps = 0
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLab14Binding.inflate(layoutInflater)
		setContentView(binding.root)
		binding.numSer.maxValue = 200
		binding.numSer.minValue = 1
		binding.numOperator.maxValue = 200
		binding.numOperator.minValue = 1
		binding.numVhod.maxValue = 200
		binding.numVhod.minValue = 1

		var xAxis: XAxis
		run {
			xAxis = binding.chart1.xAxis
			xAxis.mAxisMinimum = 0f
			xAxis.enableGridDashedLine(10f, 10f, 0f)
		}
		binding.btStart.setOnClickListener {
			binding.label4.text = ""
			val rubDataset1 = LineDataSet(mutableListOf(Entry(0F, 0f)), "customer").apply {
				circleRadius = 1f
				lineWidth = 3F
			}
			binding.chart1.data = LineData(listOf(rubDataset1))
			Model.run(binding.numVhod.value.toDouble(), binding.numSer.value.toDouble() / 100, binding.numOperator.value.toInt())
			isStarted = true
			busyOps = Model.getBusyOperatorsSize()

			job = CoroutineScope(Dispatchers.Main).launch {
				while (isStarted) {
					isStarted = Model.iter()
					if (busyOps < Model.getBusyOperatorsSize()) {
						binding.chart1.data.dataSets.first().addEntry(Entry(Model.Time.toFloat(), busyOps.toFloat()))
						busyOps++
						binding.chart1.data.dataSets.first().addEntry(Entry(Model.Time.toFloat(), busyOps.toFloat()))

					} else {
						binding.chart1.data.dataSets.first().addEntry(Entry(Model.Time.toFloat(), busyOps.toFloat()))

						busyOps--
						binding.chart1.data.dataSets.first().addEntry(Entry(Model.Time.toFloat(), busyOps.toFloat()))
					}
					binding.lbPeople.text = Model.queueSize().toString()
					binding.lbAllPeople.text = Model.CAmount.toString()
				}
			}
		}






		binding.btStop.setOnClickListener {
			isStarted = false
		}
	}

}

abstract class Agent {

	open fun getNextEventTime(): Double {
		return Double.MAX_VALUE
	}

	open fun processEvent() {}
}

class ArrivalProcess(private val bank: Bank) : Agent() {

	private val rnd = Random()
	private var nextArrivalTime = 0.0

	init {
		nextArrivalTime = simulateInterrivalTime()
	}

	private fun simulateInterrivalTime(): Double {
		return -Math.log(kotlin.random.Random.nextDouble()) / Model.lArr
	}

	override fun getNextEventTime(): Double {
		return nextArrivalTime
	}

	override fun processEvent() {
		val customer = Customer()
		bank.customerArrived(customer)
		nextArrivalTime += simulateInterrivalTime()
	}
}

class Customer : Agent()

class Bank : Agent() {

	private val service = Service(Model.opsAmount)
	private val queue = MyQueue()

	fun customerArrived(customer: Customer) {
		if (service.hasFreeOperator()) {
			service.acceptCustomer(customer)
		} else {
			queue.acceptCustomer(customer)
		}
	}

	override fun getNextEventTime(): Double {
		return service.getNextEventTime()
	}

	override fun processEvent() {
		service.processEvent()
		if (queue.hasCustomers()) {
			val cus = queue.takeCustomer()
			service.acceptCustomer(cus)
		}
	}

	fun getBusyOperatorsSize(): Int {
		return service.getBusyOperatorsSize()
	}

	fun getQueueSize(): Int {
		return queue.getQueueSize()
	}
}

class MyQueue : Agent() {

	private val queue = LinkedList<Customer>()

	fun acceptCustomer(customer: Customer) {
		queue.add(customer)
	}

	fun hasCustomers(): Boolean {
		return queue.isNotEmpty()
	}

	fun takeCustomer(): Customer {
		return queue.remove()
	}

	fun getQueueSize(): Int {
		return queue.size
	}
}

class Service(N: Int) : Agent() {

	private val operators = ArrayList<Operator>()
	private var activeOper: Agent? = Operator()

	init {
		for (i in 0 until N) {
			operators.add(Operator())
		}
	}

	fun acceptCustomer(customer: Customer) {
		findFreeOperator()?.acceptCustomer(customer)
	}

	fun hasFreeOperator(): Boolean {
		val op = findFreeOperator()
		return op != null
	}

	private fun findFreeOperator(): Operator? {
		for (op in operators) {
			if (op.isFree()) {
				return op
			}
		}
		return null
	}

	override fun getNextEventTime(): Double {
		var tMin = Double.MAX_VALUE
		activeOper = null
		for (op in operators) {
			val tA = op.getNextEventTime()
			if (tA < tMin) {
				tMin = tA
				activeOper = op
			}
		}
		return tMin
	}

	override fun processEvent() {
		activeOper?.processEvent()
	}

	fun getBusyOperatorsSize(): Int {
		var size = 0
		for (op in operators) {
			if (!op.isFree()) {
				size++
			}
		}
		return size
	}
}

class Operator : Agent() {

	private var customerInService: Customer? = null
	private var endOfSeviceTime = Double.MAX_VALUE
	private val rnd = Random()

	fun acceptCustomer(customer: Customer) {
		if (isFree()) {
			customerInService = customer
			endOfSeviceTime = Model.Time + simulateServiceTime()
		}
	}

	private fun simulateServiceTime(): Double {
		return -Math.log(kotlin.random.Random.nextDouble()) / Model.lOper
	}

	fun isFree(): Boolean {
		return customerInService == null
	}

	override fun getNextEventTime(): Double {
		return endOfSeviceTime
	}

	override fun processEvent() {
		customerInService = null
		endOfSeviceTime = Double.MAX_VALUE
	}
}

object Model {

	private val agents = ArrayList<Agent>()
	private val bank: Bank = Bank()
	private val arrivalProcess: ArrivalProcess = ArrivalProcess(bank)
	private var T = 0.0
	val Time: Double
		get() = T
	private var activeAgent: Agent? = null
	private var customersAmount = 0
	val CAmount: Int
		get() = customersAmount
	private var lambdaArrival = 0.0
	val lArr: Double
		get() = lambdaArrival
	private var lambdaOperator = 0.0
	val lOper: Double
		get() = lambdaOperator
	private var operatorsAmount = 0
	val opsAmount: Int
		get() = operatorsAmount

	fun run(lambdaArr: Double, lambdaOper: Double, opsAmount: Int) {
		lambdaArrival = lambdaArr
		lambdaOperator = lambdaOper
		operatorsAmount = opsAmount
		agents.add(bank)
		agents.add(arrivalProcess)
		T = 0.0
	}

	fun iter(): Boolean {
		if (continueCondition(T)) {
			var tMin = Double.MAX_VALUE
			activeAgent = null
			for (agent in agents) {
				val tAgent = agent.getNextEventTime()
				if (tAgent < tMin) {
					tMin = tAgent
					activeAgent = agent
				}
			}
			T = tMin
			activeAgent?.processEvent()
			customersAmount++
			return true
		}
		return false
	}

	fun continueCondition(t: Double): Boolean {
		return t < 100
	}

	fun queueSize(): Int {
		return bank.getQueueSize()
	}

	fun getBusyOperatorsSize(): Int {
		return bank.getBusyOperatorsSize()
	}
}


