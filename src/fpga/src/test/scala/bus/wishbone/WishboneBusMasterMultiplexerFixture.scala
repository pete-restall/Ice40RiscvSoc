package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusMasterMultiplexer

class WishboneBusMasterMultiplexerFixture(busConfig: WishboneConfig, numberOfMasters: Int, dutCreatedViaApplyFactory: Boolean) extends Component {
	val io = new WishboneBusMasterMultiplexer.IoBundle(busConfig, numberOfMasters)
	private val dut = createAndWireDut()

	private def createAndWireDut() = if (dutCreatedViaApplyFactory) createWiredDutViaApplyFactory() else createAndWireDutManually()

	private def createWiredDutViaApplyFactory() = {
		val dut = WishboneBusMasterMultiplexer(io.selector, io.masters.head, io.masters.tail.toSeq:_*)
		io.slave <> dut.io.slave
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new WishboneBusMasterMultiplexer(busConfig, numberOfMasters)
		io <> dut.io
		dut
	}

	def anyAddress() = Random.nextInt(1 << io.slave.ADR.getWidth)

	def anyData() = Random.nextInt(1 << io.slave.DAT_MOSI.getWidth)

	def anyMasterIndexExcept(except: Int): Int = {
		val index = anyMasterIndex()
		if (index != except) index else if (io.masters.length == 1) index ^ 1 else anyMasterIndexExcept(except)
	}

	def anyMasterIndex() = Random.nextInt(io.masters.length)

	def anySel() = Random.nextInt(1 << io.slave.SEL.getWidth)

	def anyBte() = Random.nextInt(1 << io.slave.BTE.getWidth)

	def anyCti() = Random.nextInt(1 << io.slave.CTI.getWidth)

	def anyTga() = Random.nextInt(1 << io.slave.TGA.getWidth)

	def anyTgc() = Random.nextInt(1 << io.slave.TGC.getWidth)

	def anyTagData() = Random.nextInt(1 << io.slave.TGD_MOSI.getWidth)
}
