package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.ValueBitWidthExtensions._
import uk.co.lophtware.msfreference.bus.{CrossbarArbiter, MasterSlaveMap}
import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusCrossbarMultiplexer
import uk.co.lophtware.msfreference.tests.HaveIoTestDoubles

class WishboneBusCrossbarMultiplexerFixture(busConfig: WishboneConfig, numberOfMasters: Int, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new WishboneBusCrossbarMultiplexerFixture.IoBundle(busConfig, numberOfMasters, numberOfSlaves)

	val allMasterAndSlaveCombinations = for (master <- io.masters; slave <- io.slaves) yield (master, slave)

	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		var mappings = allMasterAndSlaveCombinations.zipWithIndex.map { case ((master, slave), index) =>
			(master, (bus: Wishbone) => io.selector === index, slave)
		}

		var arbiter = HaveIoTestDoubles.stubFor(new CrossbarArbiter.IoBundle(numberOfMasters, numberOfSlaves))
		arbiter.io.slaves.foreach { slave =>
			slave.grantedMasterIndex := io.selector / numberOfSlaves
			slave.encoder.inputs.foreach(_ := False)
			slave.masters.foreach { master =>
				master.isGranted := False
				master.isStalled := False
				master.isError := False
			}
		}

		WishboneBusCrossbarMultiplexer(MasterSlaveMap(mappings.head, mappings.tail.toSeq:_*), arbiter)
	}

	private def createAndWireDutManually() = {
		var multiplexer = new WishboneBusCrossbarMultiplexer(busConfig, numberOfMasters, numberOfSlaves)
		multiplexer.io.masters.zipWithIndex.foreach { case (master, masterIndex) =>
			master.requestedSlaveIndex := io.selector % numberOfMasters
			master.wishbone << io.masters(masterIndex)
		}

		multiplexer.io.slaves.zipWithIndex.foreach { case (slave, slaveIndex) =>
			slave.grantedMasterIndex := io.selector / numberOfSlaves
			slave.wishbone >> io.slaves(slaveIndex)
		}

		multiplexer
	}
}

object WishboneBusCrossbarMultiplexerFixture {
	case class IoBundle(private val busConfig: WishboneConfig, private val numberOfMasters: Int, private val numberOfSlaves: Int) extends Bundle {
		val masters = Seq.fill(numberOfMasters) { spinal.lib.slave(new Wishbone(busConfig)) }
		val slaves = Seq.fill(numberOfSlaves) { spinal.lib.master(new Wishbone(busConfig)) }
		val selector = in UInt((numberOfMasters * numberOfSlaves).toCombinationalBitWidth)
	}
}
