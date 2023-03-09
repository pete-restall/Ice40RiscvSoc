package uk.co.lophtware.msfreference.memory.flashqspi

import spinal.core._
import spinal.lib.{slave, Stream}
import spinal.lib.fsm._

class FlashQspiMemoryStateMachine extends StateMachine {
	val io = new FlashQspiMemoryStateMachine.IoBundle()

	private val isBitBangingGranted = Reg(Bool()) init(False)
	io.bitBanger.isBitBangingGranted := isBitBangingGranted

	private val isQspiGranted = Reg(Bool()) init(False)
	isQspiGranted := // TODO: CURRENTLY ASSUMES ONLY isBitBangingGranted; WHEN !isBitBangingGranted, USE False
		(io.bitBanger._Cs && io.bitBanger.isQspiRequested) ||
		(!io.bitBanger._Cs && isQspiGranted) ||
		(!io.bitBanger._Cs && !isQspiGranted && io.bitBanger.isQspiRequested)

	io.bitBanger.isQspiGranted := isQspiGranted

	io.bitBanger.isTransactionValid := isBitBangingGranted && io.bitBanger.isQspiRequested === isQspiGranted

	io.driver._Cs := isBitBangingGranted.clockDomain.isResetActive || io.bitBanger._Cs // TODO: && isBitBangingGranted || whateverLogicFastReadUses

	private val reset = Reg(Bool()) init(True)
	io.driver.reset := reset

	io.driver.transaction.isQspi := io.bitBanger.isQspiGranted // TODO: && isBitBangingGranted || whateverLogicFastReadUses
	io.driver.transaction.isWriteProtected := isBitBangingGranted.clockDomain.isResetActive || !io.bitBanger._Wp // TODO: && isBitBangingGranted
	io.driver.transaction.mosi := 0 // TODO - THIS IS NOT FOR BIT-BANGING; IT'S FOR THE FAST READ...
	io.driver.transaction.readWriteStrobe.valid := False // TODO - THIS IS NOT FOR BIT-BANGING; IT'S FOR THE FAST READ...
	io.driver.transaction.readWriteStrobe.payload := False // TODO - THIS IS NOT FOR BIT-BANGING; IT'S FOR THE FAST READ...

	private val initialState = new State with EntryPoint {
		onEntry {
			reset := True
			isBitBangingGranted := True
		}

		whenIsActive {
			reset := False
			when(!isBitBangingGranted || !io.bitBanger.isBitBangingRequested && io.driver.transaction.readWriteStrobe.ready) { // TODO: TRY AND GET RID OF !isBitBangingGranted - ONLY REQUIRED TO SATISFY INDUCTANCE
				goto(fastReadIdleState)
			}
		}
	}

	private val fastReadIdleState: State = new State {
		onEntry {
			isBitBangingGranted := False
		}

		whenIsActive {
			when(isBitBangingGranted || io.bitBanger.isBitBangingRequested && io.driver.transaction.readWriteStrobe.ready) { // TODO: TRY AND GET RID OF isBitBangingGranted - ONLY REQUIRED TO SATISFY INDUCTANCE
				goto(initialState)
			}
		}
	}

// TODO: ENSURE NO WRITES CAN BE MADE AFTER THE FIRST QSPI READ, UNTIL /CS HAS GONE HIGH - THIS WILL NEED TO BE A SEPARATE CLASS BECAUSE THE STATE MACHINE DOES NOT HAVE THE R/W FLAG, FlashQspiMemoryShortedQspiGuard

// for bit-banging:
//   serdes._Cs is controller._Cs; use serdes._Cs as the register's R-value
//   serdes.transaction.isWriteProtected = !controller._Wp and can only change when _Cs is high; use !serdes.transaction.isWriteProtected as the register's R-value
//   serdes.transaction.valid is a monostable (until serdes.transaction.ready) and can only go true if (isTransactionValid & (controller.mosi.valid | controller.miso.ready))
//   serdes.transaction.isQspi is controller.isQspiGranted
//   serdes.transaction.readCount is 1 if ...
//   serdes.transaction.writeCount is 1 if ...
//   controller.mosi.ready is isTransactionValid
//   controller.miso.valid is isTransactionValid && serdes.miso.ready
//   controller.miso.payload is serdes.miso.payload
//   serdes.transaction.mosi.valid = (isTransactionValid & controller.mosi.valid); for Wishbone, if !isTransactionValid then reject the WR transaction with ERR
//   serdes.transaction.miso.ready = (isTransactionValid & controller.miso.ready); for Wishbone, if !isTransactionValid then reject the RD transaction with ERR
//   TODO: readCount and writeCount (and width of MISO and MOSI) ought to be configurable...
}

object FlashQspiMemoryStateMachine {
	case class IoBundle() extends Bundle {
		val bitBanger = new FlashQspiMemoryBitBangingController.IoBundle()
		val driver = new Bundle {
			val transaction = new FlashQspiMemoryTransactionDriver.ControllerIoBundle().flip() // needs mux so can be driven by both bit-banger and fast-reader
			val _Cs = out Bool()
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
		val isWriteProtected = in Bool() // !stateMachine.bitBanger._Wp

		val readWriteStrobe = slave(Stream(Bool())) // wishbone adapter to control this; ready = stateMachine.isTransactionValid && serdes.command.isReady
		val mosi = in UInt(8 bits) // wishbone adapter to control this
		val miso = out UInt(8 bits) // wishbone adapter to control this
	}
}

object FlashQspiMemoryBitBangingController { // TODO: THIS WILL BE THE BACKING FOR A STATUS AND/OR CONTROL REGISTER, ACCESSIBLE / CONTROLLED BY THE CPU; ANOTHER REGISTER WILL BE REQUIRED TO INITIATE THE MOSI / MISO DATA TRANSACTION
	case class IoBundle() extends Bundle {
		val _Cs = in Bool() // TODO: CONVERT THIS TO isChipSelected (active-high)
		val _Wp = in Bool() // TODO: CONVERT THIS TO isWriteProtected (active-high)

		val isQspiRequested = in Bool()
		val isQspiGranted = out Bool()

		val isBitBangingRequested = in Bool()
		val isBitBangingGranted = out Bool()

		val isTransactionValid = out Bool()
	}
}
