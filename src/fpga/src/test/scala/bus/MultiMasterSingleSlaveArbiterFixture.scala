package uk.co.lophtware.msfreference.tests.bus

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.bus.MultiMasterSingleSlaveArbiter

class MultiMasterSingleSlaveArbiterFixture(numberOfMasters: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new MultiMasterSingleSlaveArbiter.IoBundle(numberOfMasters)
	private val dut = createAndWireDut()

	// TODO: The factory method needs to be written next
	private def createAndWireDut() = /*if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else */createAndWireDutManually()
/*
	private def createWiredDutViaApplyFactory() = {
		val dut = MultiMasterSingleSlaveArbiter(io.inputs.head, io.inputs.tail.toSeq:_*)
		io.isValid := dut.io.isValid
		io.output := dut.io.output
		dut
	}
*/

	private def createAndWireDutManually() = {
		val dut = new MultiMasterSingleSlaveArbiter(numberOfMasters)
		io <> dut.io
		dut
	}

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
