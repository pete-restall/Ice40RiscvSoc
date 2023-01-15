package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}
import spinal.lib.{master, slave}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap

class WishboneBusMasterSlaveMapFixture(numberOfMasters: Int, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	private val busConfig = new WishboneConfig(addressWidth=16, dataWidth=16)

	val io = new Bundle {
		val dut = new WishboneBusMasterSlaveMap.IoBundle(numberOfMasters, numberOfSlaves)
		val wishbone = new Bundle {
			val masters = Seq.fill(numberOfMasters) { slave(new Wishbone(busConfig)) }
			val slaves = Seq.fill(numberOfSlaves) { master(new Wishbone(busConfig)) }
		}
	}

	private val masters = Seq.fill(numberOfMasters) { master(new Wishbone(busConfig)) }
	masters.zip(io.wishbone.masters).foreach(x => x._1 <> x._2)

	private val slaves = Seq.fill(numberOfSlaves) { slave(new Wishbone(busConfig)) }
	slaves.zip(io.wishbone.slaves).foreach(x => x._1 <> x._2)

	val outOfBoundsSlaveIndex = slaves.length

	val multipleSlavesSelectedIndex = -1

	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val map = for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) yield {
			(master._1, stubMasterToSlaveMapping(master, slave), slave._1)
		}

		WishboneBusMasterSlaveMap(map.head, map.tail:_*)
	}

	private def stubMasterToSlaveMapping(master: (Wishbone, Int), slave: (Wishbone, Int)): Wishbone => Bool = {
		(m: Wishbone) => m.ADR === addressFor(master._2, slave._2) || m.ADR === addressFor(master._2, multipleSlavesSelectedIndex)
	}

	def addressFor(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)

	private def createAndWireDutManually() = {
		val dut = new WishboneBusMasterSlaveMap(io.wishbone.masters, io.wishbone.slaves)
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
