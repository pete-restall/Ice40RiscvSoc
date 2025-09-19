package net.restall.ice40riscvsoc.memory.flashqspi

import spinal.core._
import spinal.lib.{master, slave, Stream}

class FlashQspiMemory(qspiClockDomain: ClockDomain) extends Component {
	val io = new FlashQspiMemory.IoBundle()

	// TODO: Use a StateMachine to control the io.qspi
	// TODO: When io.isWrite && io.mosi.valid, send memory write (multiple transactions) to io.qspi
	// TODO: When !io.isWrite && io.miso.ready, send memory read (multiple transactions) to io.qspi
	// TODO: This doesn't really work.  Need a way to read data, write data, read registers, write registers, execute commands...
}

object FlashQspiMemory {
	case class IoBundle() extends Bundle {
		val master = new MasterIoBundle()
		val serdes = new FlashQspiMemorySerdes.IoBundle().flip()
	}

	case class MasterIoBundle() extends Bundle {
		val control = new FlashQspiMemoryBitBangingController.IoBundle()
		val data = new DataIoBundle()
	}

	case class DataIoBundle() extends Bundle {
		val readWriteStrobe = Stream(Bool()) // wishbone adapter to control this; ready = stateMachine.isTransactionValid && serdes.command.isReady
		val mosi = in UInt(8 bits) // wishbone adapter to control this
		val miso = out UInt(8 bits) // wishbone adapter to control this
	}
}
