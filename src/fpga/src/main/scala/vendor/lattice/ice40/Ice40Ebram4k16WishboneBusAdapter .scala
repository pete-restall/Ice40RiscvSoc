package uk.co.lophtware.msfreference.vendor.lattice.ice40

import spinal.core._
import spinal.lib.bus.wishbone._
import spinal.lib.slave

class Ice40Ebram4k16WishboneBusAdapter extends Component {
	val io = new Ice40Ebram4k16WishboneBusAdapter.IoBundle()
	noIoPrefix()

	val isNotReadWaitState = RegNext(!io.wishbone.WE && io.wishbone.STB) init(False)
	io.wishbone.ACK := io.wishbone.CYC && ((io.wishbone.WE && io.wishbone.STB) || isNotReadWaitState)
	io.wishbone.DAT_MISO := io.ebram.DO
	io.ebram.ADR := io.wishbone.ADR
	io.ebram.ADW := io.wishbone.ADR

	io.ebram.DI := io.wishbone.DAT_MOSI

	io.ebram.MASK_N := ~io.wishbone.SEL
	io.ebram.RE := io.wishbone.STB
	io.ebram.WE := io.wishbone.STB
	io.ebram.CER := io.wishbone.CYC && !io.wishbone.WE
	io.ebram.CEW := io.wishbone.CYC && io.wishbone.WE
}

object Ice40Ebram4k16WishboneBusAdapter {
	case class IoBundle() extends Bundle {
		val wishbone = slave(new Wishbone(wishboneConfig))

		val ebram = new Bundle {
			val DI = out Bits(16 bits)
			val ADW = out UInt(8 bits)
			val ADR = out UInt(8 bits)
			val CEW = out Bool()
			val CER = out Bool()
			val RE = out Bool()
			val WE = out Bool()
			val MASK_N = out Bits(16 bits)
			val DO = in Bits(16 bits)
		}
	}

	val wishboneConfig = new WishboneConfig(
		addressWidth=8,
		dataWidth=16,
		selWidth=16,
		tgaWidth=0,
		tgcWidth=0,
		tgdWidth=0,
		useBTE=false,
		useCTI=false,
		useERR=false,
		useLOCK=false,
		useRTY=false,
		useSTALL=false)
}
