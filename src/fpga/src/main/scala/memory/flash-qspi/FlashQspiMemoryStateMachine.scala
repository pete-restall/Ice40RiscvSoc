package net.restall.ice40riscvsoc.memory.flashqspi

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

	private val mustTransitionToInitialState = isBitBangingGranted || io.bitBanger.isBitBangingRequested && !isTransactionInProgress

	private val isFastReadChipSelected = Reg(Bool()) init(False)
	io.driver.isChipSelected := !reset && (
		isTransactionInProgress ||
		isBitBangingGranted && io.bitBanger.isChipSelected ||
		!isBitBangingGranted && isFastReadChipSelected)

	private val isFastReaderReady = Reg(Bool()) init(False)
	io.fastReader.ready := !isTransactionInProgress && !mustTransitionToInitialState && isFastReaderReady

	private val transactionStrobe = Reg(Bool()) init(False)
	io.driver.transaction.readWriteStrobe.valid := transactionStrobe

	private val isWriteTransaction = Reg(Bool()) init(False)
	io.driver.transaction.readWriteStrobe.payload := isWriteTransaction

	private val transactionMosi = Reg(UInt(8 bits)) init(0xff)
	io.driver.transaction.mosi := transactionMosi

	io.driver.transaction.isQspi := io.bitBanger.isQspiGranted // TODO: && isBitBangingGranted || whateverLogicFastReadUses
	io.driver.transaction.isWriteProtected := isBitBangingGranted.clockDomain.isResetActive || io.bitBanger.isWriteProtected // TODO: && isBitBangingGranted; must always be True when !isBitBangingGranted

	private val initialState = new State with EntryPoint {
		onEntry {
			reset := True
			isBitBangingGranted := True
			isFastReadChipSelected := False
			isFastReaderReady := False
			transactionStrobe := False
			transactionMosi := 0xff
		}

		whenIsActive {
			reset := False
			isFastReadChipSelected := False
			isFastReaderReady := False
			transactionStrobe := False
			isQspiGranted :=
				(!io.bitBanger.isChipSelected && io.bitBanger.isQspiRequested) ||
				(io.bitBanger.isChipSelected && isQspiGranted) ||
				(io.bitBanger.isChipSelected && !isQspiGranted && io.bitBanger.isQspiRequested)

			when(!isBitBangingGranted || !io.bitBanger.isBitBangingRequested && !isTransactionInProgress) { // TODO: TRY AND GET RID OF !isBitBangingGranted - ONLY REQUIRED TO SATISFY INDUCTANCE
				goto(fastReadEntryState)
			}
		}
	}

	private val nextAddress = Reg(UInt(24 bits)) init(0)

	private val fastReadEntryState: State = new State {
		onEntry {
			reset := False
			isBitBangingGranted := False
			isFastReaderReady := False
			isFastReadChipSelected := False
			isQspiGranted := False
		}

		whenIsActive {
			reset := False
			isBitBangingGranted := False
			isFastReadChipSelected := True
			isFastReaderReady := False
			isQspiGranted := False
			isWriteTransaction := True
			transactionStrobe := True
			transactionMosi := 0xeb

			when(mustTransitionToInitialState) {
				goto(initialState)
			}

			nextAddress := 0
			// TODO: SEND ADDRESS OF 0x000000 AND THE DUMMY BYTES TO THE FLASH (AND MAKE SURE TRISTATE IS ACTIVE BEFORE THE FALLING EDGE) BUT DO NOT START THE READ; THIS MEANS THE INITIAL READ CAN START FROM 0.  PROBABLY BEST MAKE THE STARTING ADDRESS CONFIGURABLE, TOO

			when(!mustTransitionToInitialState && io.driver.transaction.readWriteStrobe.fire) {
				transactionStrobe := False
				goto(fastReadSendAddressState)
			}
		}
	}

	private val fastReadSendAddressState: State = new State {
		private var addressCounter = Reg(UInt(3 bits)) init(0)

		onEntry {
			reset := False
			isFastReaderReady := False
			isQspiGranted := True
			isWriteTransaction := True
			transactionStrobe := False
			addressCounter := 0
		}

		whenIsActive {
			reset := False
			isFastReaderReady := False
			isFastReadChipSelected := True
			isQspiGranted := True
			isWriteTransaction := True
			transactionStrobe := True

// TODO: send the address bytes followed by the number of read bytes to get MISO populated

//			isFastReaderReady := False

			when(mustTransitionToInitialState) {
				goto(initialState)
			} elsewhen(io.driver.transaction.readWriteStrobe.fire && addressCounter === 5) {
				goto(fastReadIdleState)
			} elsewhen(io.driver.transaction.readWriteStrobe.fire) {
				addressCounter := addressCounter + 1
			}
		}
	}

	private val fastReadIdleState: State = new State {
		onEntry {
			reset := False
			isFastReadChipSelected := True
			isFastReaderReady := True
			isQspiGranted := True
			isWriteTransaction := False
			transactionStrobe := False
		}

		whenIsActive {
			reset := False

			isFastReaderReady := True
			isQspiGranted := True
			isWriteTransaction := False
			transactionStrobe := False

			when(mustTransitionToInitialState) {
				goto(initialState)
			}

			when(io.fastReader.fire) {
				nextAddress := io.fastReader.payload + 1
			}

			when(!mustTransitionToInitialState && io.fastReader.fire && io.fastReader.payload === nextAddress) {
				//goto(fastReadByteState) // can avoid the state transition by handling the transaction here...
			}

			when(!mustTransitionToInitialState && io.fastReader.fire && io.fastReader.payload =/= nextAddress) {
				goto(dirtyAddressState)
			}
		}

//		onExit { isFastReadChipSelected := False } // TODO: CAN THIS BE REMOVED ?
	}

	private val dirtyAddressState: State = new State {
		onEntry {
			reset := False
			isFastReadChipSelected := False
			isFastReaderReady := False
			isQspiGranted := True
			transactionStrobe := False
		}

		whenIsActive {
			reset := False
			isFastReaderReady := False
			isFastReadChipSelected := False
			isQspiGranted := True
			transactionStrobe := False

			when(mustTransitionToInitialState) {
				goto(initialState)
			}

			when(!mustTransitionToInitialState && !isTransactionInProgress) {
				goto(fastReadSendAddressState)
			}
		}
	}

	private val fastReadByteState: State = new State {
		onEntry {
			reset := False
			isFastReadChipSelected := True
			isFastReaderReady := False
			isQspiGranted := True
		}

		whenIsActive {
			reset := False
			isFastReadChipSelected := True
			isFastReaderReady := False
			isQspiGranted := True

			when(mustTransitionToInitialState) {
				goto(initialState)
			}

			when(!mustTransitionToInitialState && !isTransactionInProgress) {
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
// TODO: WHAT WE REALLY WANT IS TO BE ABLE TO SEND / RECEIVE MULTIPLE BYTES IN ONE TRANSACTION - THE SERDES MIGHT BE RUNNING A LOT FASTER, SO ONE BYTE AT A TIME IS A WASTE... WOULD BE GOOD TO SUPPLY A VARIABLE FOR THE BUFFER SIZE, THOUGH; 1-7 bytes
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
