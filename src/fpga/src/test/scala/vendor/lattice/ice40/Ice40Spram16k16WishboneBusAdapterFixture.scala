package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._

import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Spram16k16WishboneBusAdapter

class Ice40Spram16k16WishboneBusAdapterFixture extends Component {
	val io = new Ice40Spram16k16WishboneBusAdapter.IoBundle()
	private val dut = new Ice40Spram16k16WishboneBusAdapter()
	io <> dut.io

	def anyAddress(): Int = Random.nextInt(1 << io.wishbone.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.wishbone.DAT_MOSI.getWidth)
}
