package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSlaveMultiplexer

class WishboneBusSlaveMultiplexerFixture(busConfig: WishboneConfig, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new WishboneBusSlaveMultiplexer.IoBundle(busConfig, numberOfSlaves)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = WishboneBusSlaveMultiplexer(io.selector, io.slaves.head, io.slaves.tail.toSeq:_*)
		io.master <> dut.io.master
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new WishboneBusSlaveMultiplexer(busConfig, numberOfSlaves)
		io <> dut.io
		dut
	}

	def anyAddress() = Random.nextInt(1 << io.master.ADR.getWidth)

	def anyData() = Random.nextInt(1 << io.master.DAT_MOSI.getWidth)

	def anySlaveIndexExcept(except: Int): Int = {
		val index = anySlaveIndex()
		if (index != except) index else if (io.slaves.length == 1) index ^ 1 else anySlaveIndexExcept(except)
	}

	def anySlaveIndex() = Random.nextInt(io.slaves.length)

	def anySel() = Random.nextInt(1 << io.master.SEL.getWidth)
}
