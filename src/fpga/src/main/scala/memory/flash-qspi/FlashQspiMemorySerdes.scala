package uk.co.lophtware.msfreference.memory.flashqspi

import spinal.core._
import spinal.lib.{master, slave, Stream}

import uk.co.lophtware.msfreference.pins.TristatePin

// TODO: An empty transaction (writeCount=readCount=0) will revert to the default pin states (tristate and output values) to facilitate state resets when toggling /CS
class FlashQspiMemorySerdes extends Component {
	val io = new FlashQspiMemorySerdes.IoBundle()
	noIoPrefix()

	private val isReadTransaction = Bool()
	isReadTransaction := (io.transaction.command.payload.readCount =/= 0 && io.transaction.command.valid)

	private val isWriteTransaction = Bool()
	isWriteTransaction := (io.transaction.command.payload.writeCount =/= 0 && io.transaction.mosi.valid) // TODO: && io.transaction.command.valid

	private val isQspi = Reg(Bool()) init(False)
	private val writeCountRemaining = Reg(UInt(io.transaction.command.writeCount.getWidth bits)) init(0)
	private val readCountRemaining = Reg(UInt(io.transaction.command.readCount.getWidth bits)) init(0)
	private val isMosiFull = Reg(Bool()) init(False)

	private val hasMoreMosi = Reg(Bool()) init(False)
	private val mosi = Reg(UInt(8 bits)) init(0)
	private val hasMoreMiso = Reg(Bool()) init(True)
	private val bitCounter = Reg(UInt(3 bits)) init(0)

	private val isIo0MosiTristated = Reg(Bool()) init(False)

	private val bitCounterWillIncrement = Bool()
	bitCounterWillIncrement := (writeCountRemaining =/= 0 && hasMoreMosi) || (writeCountRemaining === 0 && readCountRemaining =/= 0 && hasMoreMiso)

	private val isStartOfTransaction = Bool()
	isStartOfTransaction := writeCountRemaining === 0 && readCountRemaining === 0 && io.transaction.command.valid

	private val resetToDefaultSpi = RegNext(isStartOfTransaction && (io.transaction.command.payload.writeCount === 0 || io.transaction.command.payload.readCount === 0)) init(True)

	private val nextBitCounterValue = UInt(3 bits)
	nextBitCounterValue := bitCounter + (isQspi ? U(4) | U(1))

	private val bitCounterWillOverflowIfIncremented = Bool()
	bitCounterWillOverflowIfIncremented := nextBitCounterValue === 0

	when(!isMosiFull && io.transaction.mosi.valid) {
		isMosiFull := True
		mosi := io.transaction.mosi.payload
	}

	when((nextBitCounterValue === 7 && !isQspi) || (nextBitCounterValue === 4 && isQspi) && bitCounterWillIncrement) {
		isMosiFull := False
	}

	when(isStartOfTransaction) {
		isQspi := io.transaction.command.isQspi
	}

	when(isStartOfTransaction && (io.transaction.command.payload.writeCount =/= 0 || io.transaction.command.payload.readCount =/= 0)) {
		// TODO: JUST MAKE A SINGLE REGISTER WITH THE DATA BEING THE ENTIRE PAYLOAD...BUT THIS CAN BE A REFACTORING AFTER THE FUNCTIONALITY HAS BEEN WRITTEN
		writeCountRemaining := io.transaction.command.payload.writeCount
		readCountRemaining := io.transaction.command.payload.readCount
		hasMoreMosi := io.transaction.mosi.valid
	}

	when(bitCounterWillOverflowIfIncremented && writeCountRemaining =/= 0) {
		writeCountRemaining := writeCountRemaining - 1
		hasMoreMosi := io.transaction.mosi.valid
	}

	when(!bitCounterWillIncrement && writeCountRemaining =/= 0) {
		hasMoreMosi := io.transaction.mosi.valid // TODO: WHEN REFACTORING, SEE IF THIS REGISTER CAN BE REMOVED
	}

	when(bitCounterWillOverflowIfIncremented && writeCountRemaining === 0) {
		readCountRemaining := readCountRemaining - 1
		hasMoreMiso := io.transaction.miso.ready
	}

	when(bitCounterWillIncrement) {
		bitCounter := nextBitCounterValue
	}

	clockDomain.withRevertedClockEdge() {
		val mosiOutBits = Reg(Bits(4 bits)) init(B("1011"))
		switch(isQspi ## bitCounter) {
			for (i <- 0 to 7) {
				is(i) {
					mosiOutBits(0) := Mux(io.pins.clockEnable, mosi(7 - i), mosiOutBits(0))
				}
			}

			for (i <- Seq(8, 12)) {
				is(i) {
					mosiOutBits(0) := Mux(io.pins.clockEnable, mosi(12 - i), mosiOutBits(0))
					mosiOutBits(1) := Mux(io.pins.clockEnable, mosi(13 - i), mosiOutBits(1))
					mosiOutBits(2) := Mux(io.pins.clockEnable, mosi(14 - i), mosiOutBits(2))
					mosiOutBits(3) := Mux(io.pins.clockEnable, mosi(15 - i), mosiOutBits(3))
				}
			}
		}

		val isIo0MosiTristated = Bool()
		val isIo1MisoTristated = Bool()
		val isIo2_WpTristated = Bool(false)
		val isIo3_HoldTristated = Bool(false)
		val tristated = RegNext(isIo3_HoldTristated ## isIo2_WpTristated ## isIo1MisoTristated ## isIo0MosiTristated) init(B("0010"))
		isIo0MosiTristated := (isQspi && writeCountRemaining === 0 && readCountRemaining =/= 0) || (!io.pins.clockEnable && tristated(0) && !resetToDefaultSpi)
		isIo1MisoTristated := (isQspi && writeCountRemaining === 0 && readCountRemaining =/= 0) || (!io.pins.clockEnable && tristated(1) && !resetToDefaultSpi) || !isQspi

		io.pins.io0Mosi.outValue := mosiOutBits(0)
		io.pins.io0Mosi.isTristated := tristated(0)

		io.pins.io1Miso.outValue := mosiOutBits(1)
		io.pins.io1Miso.isTristated := tristated(1)

		io.pins.io2_Wp.outValue := mosiOutBits(2)
		io.pins.io2_Wp.isTristated := tristated(2)

		io.pins.io3_Hold.outValue := mosiOutBits(3)
		io.pins.io3_Hold.isTristated := tristated(3)
	}

	io.pins.clockEnable := bitCounterWillIncrement
	io.transaction.miso.payload := 0
	io.transaction.miso.valid := False
	io.transaction.mosi.ready := !clockDomain.isResetActive && !isMosiFull
	io.transaction.command.ready := !clockDomain.isResetActive && !bitCounterWillIncrement // && !commandFull; remove the !bitCounterWillIncrement and add something similar to 'isMosiFull' because it will allow the command to be changed on multiple clock cycles before a transaction starts, or when waiting on a MOSI or MISO
}

object FlashQspiMemorySerdes {
	case class IoBundle() extends Bundle {
		val transaction = new TransactionIoBundle()
		val pins = new PinIoBundle()
	}

	case class TransactionIoBundle() extends Bundle {
		val command = slave(Stream(Transaction()))
		val mosi = slave(Stream(UInt(8 bits)))
		val miso = master(Stream(UInt(8 bits)))
	}

	case class Transaction() extends Bundle {
		val isQspi = in Bool()
		val writeCount = in UInt(3 bits)
		val readCount = in UInt(3 bits)
	}

	case class PinIoBundle() extends Bundle {
		val clockEnable = out Bool()
		val io0Mosi = new TristatePin.IoBundle()
		val io1Miso = new TristatePin.IoBundle()
		val io2_Wp = new TristatePin.IoBundle()
		val io3_Hold = new TristatePin.IoBundle()
	}
}
