package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import scala.util.Random

import spinal.core._

import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k16WishboneBusAdapter

class Ice40Ebram4k16WishboneBusAdapterFixture extends Component {
	val io = new Ice40Ebram4k16WishboneBusAdapter.IoBundle()
	private val dut = new Ice40Ebram4k16WishboneBusAdapter()
	io <> dut.io

	def anyAddress(): Int = Random.nextInt(1 << io.wishbone.ADR.getWidth)

	def anyData(): Int = Random.nextInt(1 << io.wishbone.DAT_MOSI.getWidth)
}
