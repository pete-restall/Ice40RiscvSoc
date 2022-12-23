package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone._
import spinal.lib.slave

import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Spram16k16, Ice40Spram16k16WishboneBusAdapter}

class Ice40Spram16k16WishboneBusAdapterFactoryFixture extends Component {
	val io = new Bundle {
		val adapter = slave(new Wishbone(Ice40Spram16k16WishboneBusAdapter.wishboneConfig))
		val spram = new Bundle {
			val AD = out UInt(14 bits)
			val DI = out Bits(16 bits)
			val MASKWE = out UInt(4 bits)
			val WE = out Bool()
			val CS = out Bool()
		}
	}

	private val spram = new Ice40Spram16k16()
	spram.io.STDBY := False
	spram.io.SLEEP := False
	spram.io.PWROFF_N := True

	io.spram.AD := spram.io.AD
	io.spram.DI := spram.io.DI
	io.spram.MASKWE := spram.io.MASKWE
	io.spram.WE := spram.io.WE
	io.spram.CS := spram.io.CS

	private val dut = Ice40Spram16k16WishboneBusAdapter(spram)
	io.adapter <> dut.io.wishbone

	def anyAddress(): Int = Random.nextInt(1 << io.adapter.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.adapter.DAT_MOSI.getWidth)

	def anyWriteMask(): Int = Random.nextInt(1 << io.adapter.SEL.getWidth)
}
