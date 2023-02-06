package uk.co.lophtware.msfreference.tests.bus

import scala.util.Random

import spinal.core._
import spinal.core.sim._
import spinal.lib.{master, slave}

import uk.co.lophtware.msfreference.bus.MasterSlaveMap

class MasterSlaveMapFixture(numberOfMasters: Int, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new Bundle {
		val dut = new MasterSlaveMap.IoBundle(numberOfMasters, numberOfSlaves)
		val bus = new Bundle {
			val masters = Seq.fill(numberOfMasters) { slave(new FakeBus()) }
			val slaves = Seq.fill(numberOfSlaves) { master(new FakeBus()) }
		}
	}

	private val masters = Seq.fill(numberOfMasters) { master(new FakeBus()) }
	masters.zip(io.bus.masters).foreach(x => x._1 <> x._2)

	private val slaves = Seq.fill(numberOfSlaves) { slave(new FakeBus()) }
	slaves.zip(io.bus.slaves).foreach(x => x._1 <> x._2)

	val outOfBoundsSlaveIndex = slaves.length

	val multipleSlavesSelectedIndex = -1

	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val map = for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) yield {
			(master._1, stubMasterToSlaveMapping(master, slave), slave._1)
		}

		MasterSlaveMap(map.head, map.tail:_*)
	}

	private def stubMasterToSlaveMapping(master: (FakeBus, Int), slave: (FakeBus, Int)): FakeBus => Bool = {
		(bus: FakeBus) => bus.address === addressFor(master._2, slave._2) || bus.address === addressFor(master._2, multipleSlavesSelectedIndex)
	}

	def addressFor(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)

	private def createAndWireDutManually() = {
		val dut = new MasterSlaveMap[FakeBus](io.bus.masters, io.bus.slaves)
		for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) {
			dut.io.masters(master._2).slaveSelects(slave._2) := stubMasterToSlaveMapping(master, slave)(master._1)
		}

		dut
	}

	io.dut.masters.zipWithIndex.foreach { master =>
		master._1.index := dut.io.masters(master._2).index
		master._1.isValid := dut.io.masters(master._2).isValid
	}
}
