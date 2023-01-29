package uk.co.lophtware.msfreference.bus

import spinal.core._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.bus.MultiMasterSingleSlaveArbiter

class CrossbarArbiter(numberOfMasters: Int, numberOfSlaves: Int) extends Component {
	val io = new CrossbarArbiter.IoBundle(numberOfMasters, numberOfSlaves)
	noIoPrefix()

	private val slaveArbiters = io.slaves.zipWithIndex.map { case (slave, slaveIndex) =>
		val slaveArbiter = new MultiMasterSingleSlaveArbiter(numberOfMasters)
		slave <> slaveArbiter.io
		slaveArbiter
	}
}

object CrossbarArbiter {
	class IoBundle(numberOfMasters: Int, numberOfSlaves: Int) extends Bundle {
		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of masters must be at least 1")
		}

		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of slaves must be at least 1")
		}

		val slaves = Seq.fill(numberOfSlaves) { new MultiMasterSingleSlaveArbiter.IoBundle(numberOfMasters) }
	}
}
