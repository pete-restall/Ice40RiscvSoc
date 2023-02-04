package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.bus.MultiMasterSingleSlaveArbiter
import uk.co.lophtware.msfreference.bus.wishbone.{WishboneBusCrossbarArbiter, WishboneBusMasterSlaveMap}
import uk.co.lophtware.msfreference.tests.bus.CrossbarArbiterFixtureTraits

class WishboneBusCrossbarArbiterFixture(busMapFactory: => WishboneBusMasterSlaveMap, dutCreatedViaApplyFactory: Boolean) extends Component with CrossbarArbiterFixtureTraits {
	private val busMap = busMapFactory

	override val io = new WishboneBusCrossbarArbiterFixture.IoBundle(busMap)

	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = WishboneBusCrossbarArbiter(busMap)

	private def createAndWireDutManually() = {
		val dut = new WishboneBusCrossbarArbiter(busMap)
		dut.io.slaves.zipWithIndex.foreach { case (slave, slaveIndex) =>
			slave.masters.zip(busMap.masters).zip(busMap.io.masters).foreach { case ((ioMaster, wbMaster), bmMaster) =>
				ioMaster.request := wbMaster.CYC && bmMaster.isValid && bmMaster.index === slaveIndex
			}
		}

		dut
	}

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
	case class IoBundle(private val busMap: WishboneBusMasterSlaveMap) extends WishboneBusCrossbarArbiter.IoBundle(busMap.masters.length, busMap.slaves.length) {
		val wishbone = new Bundle {
			val masters = busMap.masters.map(bus => spinal.lib.slave(new Wishbone(bus.config)))
			val slaves = busMap.slaves.map(bus => spinal.lib.master(new Wishbone(bus.config)))
		}

		slaves.flatMap(_.masters).foreach(_.request.flip())
	}

	def addressOf(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)

	val AllSlavesSelected = 0xffff
}
