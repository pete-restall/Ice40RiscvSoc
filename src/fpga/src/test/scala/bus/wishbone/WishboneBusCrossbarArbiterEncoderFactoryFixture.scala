package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.ValueBitWidthExtensions._
import uk.co.lophtware.msfreference.bus.wishbone.{WishboneBusCrossbarArbiter, WishboneBusMasterSlaveMap}
import uk.co.lophtware.msfreference.multiplexing.Encoder

class WishboneBusCrossbarArbiterEncoderFactoryFixture(numberOfMasters: Int, numberOfSlaves: Int) extends Component {
	private val busConfig = new WishboneConfig(addressWidth=16, dataWidth=4)
	private val masters = Seq.fill(numberOfMasters) { WishboneTestDoubles.stubMasterWith(busConfig) }
	private val slaves = Seq.fill(numberOfSlaves) { WishboneTestDoubles.stubSlaveWith(busConfig) }
	private val busMappings = for (master <- masters.zipWithIndex; slave <- slaves.zipWithIndex) yield { (
		master._1,
		(bus: Wishbone) => bus.ADR === WishboneBusCrossbarArbiterEncoderFactoryFixture.addressOf(master._2, slave._2),
		slave._1
	) }

	private val busMap = WishboneBusMasterSlaveMap(busMappings.head, busMappings.tail:_*)

	val io = new WishboneBusCrossbarArbiterEncoderFactoryFixture.IoBundle(numberOfMasters, numberOfSlaves)
	private var slaveIndex = 0
	private val dut = WishboneBusCrossbarArbiter(busMap, inputs => {
		val encoder = io.encoders(slaveIndex)
		encoder.inputs := inputs
		slaveIndex += 1
		encoder
	})

	io.arbiterEncoders.zip(dut.io.slaves).foreach { case (io, dut) =>
		io.isValid := dut.encoder.isValid
		io.output := dut.encoder.output
	}

	masters.foreach { master =>
		master.ADR := 0
		master.CYC := False
		master.DAT_MOSI := 0
		master.STB := False
		master.WE := False
	}

	slaves.foreach { slave =>
		slave.ACK := False
		slave.DAT_MISO := 0
	}

	def resetAllArbiterRequests(): Unit = {
		masters.foreach(_.CYC #= false)
		sleep(1)
	}

	def setArbiterRequest(masterIndex: Int, slaveIndex: Int, value: Boolean = true): Unit = {
		masters(masterIndex).ADR #= WishboneBusCrossbarArbiterFixture.addressOf(masterIndex, slaveIndex)
		masters(masterIndex).CYC #= value
		sleep(1)
	}
}

object WishboneBusCrossbarArbiterEncoderFactoryFixture {
	case class IoBundle(private val numberOfMasters: Int, private val numberOfSlaves: Int) extends Bundle {
		val encoders = Seq.fill(numberOfSlaves) { new Encoder.IoBundle(numberOfMasters).flip() }
		val arbiterEncoders = Seq.fill(numberOfSlaves) { new Bundle {
			val isValid = out Bool()
			val output = out UInt(numberOfMasters.toCombinationalBitWidth)
		} }
	}

	def addressOf(masterIndex: Int, slaveIndex: Int): Int = (masterIndex << 8) | (slaveIndex & 0xff)
}
