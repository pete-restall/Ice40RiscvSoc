package uk.co.lophtware.msfreference.tests.bus

import spinal.core.Component
import spinal.core.sim._

import uk.co.lophtware.msfreference.bus.{CrossbarArbiter, MultiMasterSingleSlaveArbiter}

trait CrossbarArbiterFixtureTraits extends Component {
	def io: CrossbarArbiter.IoBundle = ???

	def setMasterRequest(slave: MultiMasterSingleSlaveArbiter.IoBundle, masterIndex: Int, value: Boolean): Unit = ???

	def reset(): Unit = {
		clockDomain.assertReset()
		clockInactive()
		sleep(10)
		clockDomain.deassertReset()
		sleep(10)
	}

	def clockInactive(): Unit = {
		clockDomain.fallingEdge()
		sleep(10)
	}

	def clock(): Unit = {
		clockActive()
		clockInactive()
	}

	def clockActive(): Unit = {
		clockDomain.risingEdge()
		sleep(10)
	}
}
