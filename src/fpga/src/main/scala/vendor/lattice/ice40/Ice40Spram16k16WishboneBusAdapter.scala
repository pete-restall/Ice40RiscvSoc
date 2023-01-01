package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._
import spinal.lib.bus.wishbone._
import spinal.lib.slave

class Ice40Spram16k16WishboneBusAdapter extends Component {
	val io = new Ice40Spram16k16WishboneBusAdapter.IoBundle()
	noIoPrefix()

	io.wishbone.ACK := (RegNext(io.wishbone.STB) init(False)) && io.wishbone.CYC // TODO: THE ACK IS IMMEDIATE ON STB IF IT'S A WRITE TRANSACTION...
	io.wishbone.DAT_MISO := io.spram.DO

	io.spram.AD := io.wishbone.ADR
	io.spram.DI := io.wishbone.DAT_MOSI
	io.spram.MASKWE := io.wishbone.SEL.asUInt
	io.spram.WE := io.wishbone.WE // TODO: THIS NEEDS TO BE 'WE && STB'
	io.spram.CS := io.wishbone.CYC
}

object Ice40Spram16k16WishboneBusAdapter {
	case class IoBundle() extends Bundle {
		val wishbone = slave(new Wishbone(wishboneConfig))

		val spram = new Bundle {
			val AD = out UInt(14 bits)
			val DI = out Bits(16 bits)
			val MASKWE = out UInt(4 bits)
			val WE = out Bool()
			val CS = out Bool()
			val DO = in Bits(16 bits)
		}
	}

	val wishboneConfig = new WishboneConfig(
		addressWidth=14,
		dataWidth=16,
		selWidth=4,
		tgaWidth=0,
		tgcWidth=0,
		tgdWidth=0,
		useBTE=false,
		useCTI=false,
		useERR=false,
		useLOCK=false,
		useRTY=false,
		useSTALL=false)

	def apply(spram: Ice40Spram16k16): Ice40Spram16k16WishboneBusAdapter = {
		if (spram == null) {
			throw new IllegalArgumentException("An iCE40 SPRAM block must be specified; arg=spram, value=null")
		}

		val adapter = new Ice40Spram16k16WishboneBusAdapter()
		adapter.io.spram.DO := 0
		spram.io.AD := adapter.io.spram.AD
		spram.io.DI := adapter.io.spram.DI
		spram.io.MASKWE := adapter.io.spram.MASKWE
		spram.io.WE := adapter.io.spram.WE
		spram.io.CS := adapter.io.spram.CS

		adapter
	}
}
