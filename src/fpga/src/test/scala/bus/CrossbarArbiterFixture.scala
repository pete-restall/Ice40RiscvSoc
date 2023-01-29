package uk.co.lophtware.msfreference.tests.bus

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.bus.CrossbarArbiter

class CrossbarArbiterFixture(numberOfMasters: Int, numberOfSlaves: Int) extends Component {
	val io = new CrossbarArbiter.IoBundle(numberOfMasters, numberOfSlaves)
	private val dut = new CrossbarArbiter(numberOfMasters, numberOfSlaves)
	io <> dut.io

	def reset(): Unit = {
		clockDomain.assertReset()
		sleep(10)
		clockDomain.deassertReset()
	}

	def clock(): Unit = {
		clockActive()
		sleep(10)
		clockInactive()
		sleep(10)
	}

	def clockActive(): Unit = clockDomain.risingEdge()

	def clockInactive(): Unit = clockDomain.fallingEdge()
}
