package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.formal._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.vendor.lattice.ice40.Ice40Ebram4k16WishboneBusAdapter
import uk.co.lophtware.msfreference.tests.bus.wishbone.IHaveWishboneSlave

class Ice40Ebram4k16WishboneBusAdapterFormalVerificationFixture extends Component with IHaveWishboneSlave {
	val io = new Ice40Ebram4k16WishboneBusAdapter.IoBundle()
	private val dut = new Ice40Ebram4k16WishboneBusAdapter()
	io <> dut.io

	def withStimuli: this.type = {
		anyseq(io.wishbone.CYC)
		anyseq(io.wishbone.STB)
		anyseq(io.wishbone.ADR)
		anyseq(io.wishbone.SEL)
		anyseq(io.wishbone.WE)
		anyseq(io.wishbone.DAT_MOSI)

		anyseq(io.ebram.DO)
		this
	}

	def asWishboneSlave: Wishbone = io.wishbone
}
