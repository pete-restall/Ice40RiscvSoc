package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.bus.CrossbarArbiter

class WishboneBusCrossbarArbiter(busMap: WishboneBusMasterSlaveMap) extends Component {
	busMap.mustNotBeNull("busMap", "Wishbone Bus Master-to-Slave Mappings must be specified")

	val io = new WishboneBusCrossbarArbiter.IoBundle(busMap.masters.length, busMap.slaves.length)
	noIoPrefix()

	private val arbiter = new CrossbarArbiter(busMap.masters.length, busMap.slaves.length)
	arbiter.io.slaves.zipWithIndex.foreach { case (slave, slaveIndex) =>
		slave.encoder <> io.slaves(slaveIndex).encoder
		io.slaves(slaveIndex).grantedMasterIndex := slave.grantedMasterIndex

		busMap.masters.zipWithIndex.foreach { case (wbMaster, masterIndex) =>
			val ioMaster = io.slaves(slaveIndex).masters(masterIndex)
			slave.masters(masterIndex).request := ioMaster.request
			ioMaster.isError := slave.masters(masterIndex).isError
			ioMaster.isGranted := slave.masters(masterIndex).isGranted
			ioMaster.isStalled := slave.masters(masterIndex).isStalled
		}
	}
}

object WishboneBusCrossbarArbiter {
	class IoBundle(private val numberOfMasters: Int, private val numberOfSlaves: Int) extends CrossbarArbiter.IoBundle(numberOfMasters, numberOfSlaves) {
	}

	def apply(busMap: WishboneBusMasterSlaveMap): WishboneBusCrossbarArbiter = {
		val arbiter = new WishboneBusCrossbarArbiter(busMap)
		arbiter.io.slaves.zipWithIndex.foreach { case (slave, slaveIndex) =>
			slave.masters.zip(busMap.masters).zip(busMap.io.masters).foreach { case ((ioMaster, wbMaster), bmMaster) =>
				ioMaster.request := wbMaster.CYC && bmMaster.isValid && bmMaster.index === slaveIndex
			}
		}

		arbiter
	}
}
