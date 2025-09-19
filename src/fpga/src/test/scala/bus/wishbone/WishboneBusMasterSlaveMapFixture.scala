package net.restall.ice40riscvsoc.tests.bus.wishbone

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}
import spinal.lib.{master, slave}

import net.restall.ice40riscvsoc.bus.MasterSlaveMap
import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusMasterSlaveMap

class WishboneBusMasterSlaveMapFixture(numberOfMasters: Int, numberOfSlaves: Int) extends Component {
	private val busConfig = new WishboneConfig(addressWidth=16, dataWidth=16)

	val io = new Bundle {
		val dut = new MasterSlaveMap.IoBundle(numberOfMasters, numberOfSlaves)
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

	private val dut = createWiredDutViaApplyFactory()

	private def createWiredDutViaApplyFactory() = {
		val map = for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) yield {
			(master._1, stubMasterToSlaveMapping(master, slave), slave._1)
		}

		WishboneBusMasterSlaveMap(map.head, map.tail:_*)
	}

	private def stubMasterToSlaveMapping(master: (Wishbone, Int), slave: (Wishbone, Int)): Wishbone => Bool = {
		(bus: Wishbone) => bus.ADR === addressFor(master._2, slave._2) || bus.ADR === addressFor(master._2, multipleSlavesSelectedIndex)
	}

	def addressFor(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)

	io.dut.masters.zipWithIndex.foreach { master =>
		master._1.index := dut.io.masters(master._2).index
		master._1.isValid := dut.io.masters(master._2).isValid
	}
}
