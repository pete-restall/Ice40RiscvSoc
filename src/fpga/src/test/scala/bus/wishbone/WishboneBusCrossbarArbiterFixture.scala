package net.restall.ice40riscvsoc.tests.bus.wishbone

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MasterSlaveMap, MultiMasterSingleSlaveArbiter}
import net.restall.ice40riscvsoc.bus.wishbone.{WishboneBusCrossbarArbiter, WishboneBusMasterSlaveMap}
import net.restall.ice40riscvsoc.tests.bus.CrossbarArbiterFixtureTraits

class WishboneBusCrossbarArbiterFixture(busMapFactory: => MasterSlaveMap[Wishbone]) extends Component with CrossbarArbiterFixtureTraits {
	private val busMap = busMapFactory

	override val io = new WishboneBusCrossbarArbiterFixture.IoBundle(busMap)

	private val dut = WishboneBusCrossbarArbiter(busMap)

	io.slaves.zip(dut.io.slaves).foreach { case (ioSlave, dutSlave) =>
		ioSlave.encoder <> dutSlave.encoder
		ioSlave.grantedMasterIndex := dutSlave.grantedMasterIndex
		ioSlave.masters.zip(dutSlave.masters).foreach { case (ioMaster, dutMaster) =>
			ioMaster.request := dutMaster.request
			ioMaster.isError := dutMaster.isError
			ioMaster.isGranted := dutMaster.isGranted
			ioMaster.isStalled := dutMaster.isStalled
		}
	}

	io.wishbone.masters.zip(busMap.masters).foreach(x => x._1 >> x._2)
	io.wishbone.slaves.zip(busMap.slaves).foreach(x => x._1 << x._2)

	override def setMasterRequest(slave: MultiMasterSingleSlaveArbiter.IoBundle, masterIndex: Int, value: Boolean): Unit = {
		io.wishbone.masters(masterIndex).ADR #= WishboneBusCrossbarArbiterFixture.addressOf(masterIndex, io.slaves.indexOf(slave))
		io.wishbone.masters(masterIndex).CYC #= value
		sleep(1)
	}
}

object WishboneBusCrossbarArbiterFixture {
	case class IoBundle(private val busMap: MasterSlaveMap[Wishbone]) extends CrossbarArbiter.IoBundle(busMap.masters.length, busMap.slaves.length) {
		val wishbone = new Bundle {
			val masters = busMap.masters.map(bus => spinal.lib.slave(new Wishbone(bus.config)))
			val slaves = busMap.slaves.map(bus => spinal.lib.master(new Wishbone(bus.config)))
		}

		slaves.flatMap(_.masters).foreach(_.request.flip())
	}

	def addressOf(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)

	val AllSlavesSelected = 0xffff
}
