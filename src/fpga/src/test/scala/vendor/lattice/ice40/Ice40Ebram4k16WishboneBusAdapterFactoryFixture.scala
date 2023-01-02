package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone._
import spinal.lib.slave

import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Ebram4k, Ice40Ebram4k16WishboneBusAdapter}

class Ice40Ebram4k16WishboneBusAdapterFactoryFixture extends Component {
	val io = new Bundle {
		val adapter = slave(new Wishbone(Ice40Ebram4k16WishboneBusAdapter.wishboneConfig))
		val ebram = new Bundle {
			val DI = out Bits(16 bits)
			val ADW = out UInt(8 bits)
			val ADR = out UInt(8 bits)
			val CEW = out Bool()
			val CER = out Bool()
			val RE = out Bool()
			val WE = out Bool()
			val MASK_N = out Bits(16 bits)
		}
	}

	private val ebram = new Ice40Ebram4k(readWidth=16 bits, writeWidth=16 bits)
	io.ebram.DI := ebram.io.DI
	io.ebram.ADW := ebram.io.ADW
	io.ebram.ADR := ebram.io.ADR
	io.ebram.CEW := ebram.io.CEW
	io.ebram.CER := ebram.io.CER
	io.ebram.RE := ebram.io.RE
	io.ebram.WE := ebram.io.WE
	io.ebram.MASK_N := ebram.io.MASK_N.get

	private val dut = Ice40Ebram4k16WishboneBusAdapter(ebram)
	io.adapter <> dut.io.wishbone

	def anyAddress(): Int = Random.nextInt(1 << io.adapter.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.adapter.DAT_MOSI.getWidth)

	def anyWriteMask(): Int = Random.nextInt(1 << io.adapter.SEL.getWidth)
}
