package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import spinal.core._
import spinal.core.formal._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16WishboneBusAdapter
import net.restall.ice40riscvsoc.tests.bus.wishbone.IHaveWishboneSlave

class Ice40Spram16k16WishboneBusAdapterFormalVerificationFixture extends Component with IHaveWishboneSlave {
	val io = new Ice40Spram16k16WishboneBusAdapter.IoBundle()
	private val dut = new Ice40Spram16k16WishboneBusAdapter()
	io <> dut.io

	def withStimuli: this.type = {
		anyseq(io.wishbone.CYC)
		anyseq(io.wishbone.STB)
		anyseq(io.wishbone.ADR)
		anyseq(io.wishbone.SEL)
		anyseq(io.wishbone.WE)
		anyseq(io.wishbone.DAT_MOSI)

		anyseq(io.spram.DO)
		this
	}

	def asWishboneSlave: Wishbone = io.wishbone
}
