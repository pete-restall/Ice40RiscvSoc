package uk.co.lophtware.msfreference.memory.flashqspi

import spinal.core._
import spinal.lib.{slave, Stream}
import spinal.lib.fsm._

class FlashQspiMemoryStateMachine extends StateMachine {
	val io = new FlashQspiMemoryStateMachine.IoBundle()

	private val isBitBangingGranted = Reg(Bool()) init(False)
	io.bitBanger.isBitBangingGranted := isBitBangingGranted

	private val isQspiGranted = Reg(Bool()) init(False)
	io.bitBanger.isQspiGranted := isQspiGranted

	io.bitBanger.isTransactionValid := isBitBangingGranted && io.bitBanger.isQspiRequested === isQspiGranted

	private val reset = Reg(Bool()) init(True)
	io.driver.reset := reset

	private val isTransactionInProgress = !io.driver.transaction.readWriteStrobe.ready

	private val isFastReadChipSelected = Reg(Bool()) init(False)
	io.driver.isChipSelected := !reset && (isTransactionInProgress || isBitBangingGranted && io.bitBanger.isChipSelected || !isBitBangingGranted && isFastReadChipSelected)

	private val isFastReaderReady = Reg(Bool()) init(False)
	io.fastReader.ready := /*!isTransactionInProgress && */isFastReaderReady

	private val transactionStrobe = Reg(Bool()) init(False)

	io.driver.transaction.isQspi := io.bitBanger.isQspiGranted // TODO: && isBitBangingGranted || whateverLogicFastReadUses
	io.driver.transaction.isWriteProtected := isBitBangingGranted.clockDomain.isResetActive || io.bitBanger.isWriteProtected // TODO: && isBitBangingGranted; must always be True when !isBitBangingGranted
	io.driver.transaction.mosi := 0xff
	io.driver.transaction.readWriteStrobe.valid := transactionStrobe // TODO - THIS IS NOT FOR BIT-BANGING; IT'S FOR THE FAST READ...
	io.driver.transaction.readWriteStrobe.payload := False // TODO - THIS IS NOT FOR BIT-BANGING; IT'S FOR THE FAST READ...

	private val mustTransitionToInitialState = isBitBangingGranted || io.bitBanger.isBitBangingRequested && !isTransactionInProgress

	private val initialState = new State with EntryPoint {
		onEntry {
			reset := True
			isBitBangingGranted := True
			isFastReadChipSelected := False
			isFastReaderReady := False
transactionStrobe := False
		}

		whenIsActive {
isFastReadChipSelected := False
transactionStrobe := False
			isFastReaderReady := False
			reset := False
			isQspiGranted :=
				(!io.bitBanger.isChipSelected && io.bitBanger.isQspiRequested) ||
				(io.bitBanger.isChipSelected && isQspiGranted) ||
				(io.bitBanger.isChipSelected && !isQspiGranted && io.bitBanger.isQspiRequested)

			when(!isBitBangingGranted || !io.bitBanger.isBitBangingRequested && !isTransactionInProgress) { // TODO: TRY AND GET RID OF !isBitBangingGranted - ONLY REQUIRED TO SATISFY INDUCTANCE
				goto(fastReadEntryState)
			}
		}
	}

	private val fastReadEntryState: State = new State {
		onEntry {
			isBitBangingGranted := False
			isFastReadChipSelected := False
			isQspiGranted := False
			isFastReaderReady := False
reset := False
		}

		whenIsActive {
			isFastReaderReady := False
			isBitBangingGranted := False
			isFastReadChipSelected := True
			isQspiGranted := False
reset := False
transactionStrobe := True
			when(mustTransitionToInitialState) {
				goto(initialState)
			} elsewhen(io.driver.transaction.readWriteStrobe.fire) {
//				transactionStrobe := True
// all of the preamble - SPI fast-read command then goto fastReadSendAddressState
transactionStrobe := False
				goto(fastReadSendAddressState)
			}
		}
	}

	private val fastReadSendAddressState: State = new State {
		onEntry {
transactionStrobe := False
			isFastReaderReady := False
//isFastReadChipSelected := True
reset := False
		}

		whenIsActive {
isFastReadChipSelected := True
reset := False


transactionStrobe := True
			isFastReaderReady := False
			when(mustTransitionToInitialState) {
				goto(initialState)
			} elsewhen(io.driver.transaction.readWriteStrobe.fire) {
// send the address bytes (obviously not in an 'otherwise')
transactionStrobe := False
				goto(fastReadIdleState)
			}
		}
	}

	private val fastReadIdleState: State = new State {
		onEntry {
isFastReadChipSelected := True
isFastReaderReady := False
reset := False
transactionStrobe := False
		}

		whenIsActive {
			val nextAddress = RegNextWhen(io.fastReader.payload + 1, io.fastReader.fire) init(0)
nextAddress.setName("NEXT_ADDRESS")
			isFastReadChipSelected := True
reset := False
transactionStrobe := False

			when(mustTransitionToInitialState) {
				isFastReaderReady := False
				goto(initialState)
			} elsewhen(!isTransactionInProgress && !isFastReaderReady) {
				isFastReaderReady := True
			} elsewhen(io.fastReader.fire && io.fastReader.payload === nextAddress) {
				isFastReaderReady := False
				goto(fastReadByteState)
			} elsewhen(io.fastReader.fire && io.fastReader.payload =/= nextAddress) {
				isFastReaderReady := False
				goto(dirtyAddressState)
			}
		}
	}

	private val dirtyAddressState: State = new State {
		onEntry {
			isFastReadChipSelected := False
			isFastReaderReady := False
reset := False
		}

		whenIsActive {
			isFastReaderReady := False
			isFastReadChipSelected := False
reset := False

			when(mustTransitionToInitialState) {
				goto(initialState)
			} elsewhen(!isTransactionInProgress) {
				goto(fastReadSendAddressState)
			}
		}
	}

	private val fastReadByteState: State = new State {
		onEntry {
			isFastReadChipSelected := True
			isFastReaderReady := False
reset := False
		}

		whenIsActive {
			isFastReadChipSelected := True
			isFastReaderReady := False
reset := False

			when(mustTransitionToInitialState) {
				goto(initialState)
			} elsewhen(!isTransactionInProgress) {
				goto(fastReadIdleState)
			}
		}
	}

// TODO: ENSURE NO WRITES CAN BE MADE AFTER THE FIRST QSPI READ, UNTIL /CS HAS GONE HIGH; ADD A READ/WRITE FLAG TO FlashQspiMemoryBitBangingController.IoBundle FOR THIS, WHICH CAN BE TIED TO THE WISHBONE WR FLAG (NOT EXPOSED IN THE REGISTER)
}

object FlashQspiMemoryStateMachine {
	case class IoBundle() extends Bundle {
		val bitBanger = new FlashQspiMemoryBitBangingController.IoBundle()
		val fastReader = slave(Stream(UInt(24 bits)))
		val driver = new Bundle {
			val transaction = new FlashQspiMemoryTransactionDriver.ControllerIoBundle().flip() // needs mux so can be driven by both bit-banger and fast-reader
			val isChipSelected = out Bool()
			val reset = out Bool()
		}
	}
}

object FlashQspiMemoryTransactionDriver { // TODO: THIS WILL BE THE THING THAT CONVERTS THE STATE MACHINE'S OUTPUTS INTO THE SERDES TRANSACTION; NOTE THAT THE MOSI/MISO MUX AND SERDES ARE ENCAPSULATED IN THE TOP-LEVEL FlashQspiMemory MODULE, ALONG WITH THE STATE MACHINE, AND ALL WIRED UP IN THERE
	case class IoBundle() extends Bundle {
		val controller = new ControllerIoBundle()
		val serdes = new FlashQspiMemorySerdes.TransactionIoBundle().flip()
	}

	case class ControllerIoBundle() extends Bundle {
		val isQspi = in Bool() // stateMachine.bitBanger.isQspiGranted
		val isWriteProtected = in Bool() // !stateMachine.bitBanger.isWriteProtected

		val readWriteStrobe = slave(Stream(Bool())) // wishbone adapter to control this; ready = stateMachine.isTransactionValid && serdes.command.isReady
		val mosi = in UInt(8 bits) // wishbone adapter to control this
		val miso = out UInt(8 bits) // wishbone adapter to control this
	}
}

object FlashQspiMemoryBitBangingController { // TODO: THIS WILL BE THE BACKING FOR A STATUS AND/OR CONTROL REGISTER, ACCESSIBLE / CONTROLLED BY THE CPU; ANOTHER REGISTER WILL BE REQUIRED TO INITIATE THE MOSI / MISO DATA TRANSACTION
	case class IoBundle() extends Bundle {
		val isChipSelected = in Bool()
		val isWriteProtected = in Bool()

		val isQspiRequested = in Bool()
		val isQspiGranted = out Bool()

		val isBitBangingRequested = in Bool()
		val isBitBangingGranted = out Bool()

		val isTransactionValid = out Bool()
	}
}
