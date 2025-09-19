package net.restall.ice40riscvsoc.memory.flashqspi

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}
import spinal.lib.slave

import net.restall.ice40riscvsoc.memory.flashqspi.FlashQspiMemory

class FlashQspiMemoryWishboneBusAdapter(addressWidth: BitCount, dataWidth: BitCount) extends Component {
	val io = new FlashQspiMemoryWishboneBusAdapter.IoBundle(addressWidth, dataWidth)

	// TODO: Use a StreamWidthAdapter to adapt from the 32-bit vexriscv bus to the io.flash
}

object FlashQspiMemoryWishboneBusAdapter {
	class IoBundle(private val addressWidth: BitCount, private val dataWidth: BitCount) extends Bundle {
		// TODO: dataWidth not a multiple of 8 is not allowed

		val flash = new FlashQspiMemory.MasterIoBundle().flip()

		val wishbone = slave(new Wishbone(new WishboneConfig(
			addressWidth=addressWidth.value,
			dataWidth=dataWidth.value,
			selWidth=dataWidth.value / 8,
			tgaWidth=0,
			tgcWidth=1,
			tgdWidth=0,
			useBTE=false,
			useCTI=false,
			useERR=false,
			useLOCK=false,
			useRTY=false,
			useSTALL=false)))
	}
}
