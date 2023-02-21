package uk.co.lophtware.msfreference.memory.flashqspi

import spinal.core._
import spinal.lib.{master, slave, Stream}

import uk.co.lophtware.msfreference.pins.TristatePin

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

	private val nextBitCounterValue = UInt(3 bits)
	nextBitCounterValue := bitCounter + (isQspi ? U(4) | U(1))

	private val bitCounterWillOverflowIfIncremented = Bool()
	bitCounterWillOverflowIfIncremented := nextBitCounterValue === 0

	when(!isMosiFull && io.transaction.mosi.valid) {
		isMosiFull := True
		mosi := io.transaction.mosi.payload
	}

	when((nextBitCounterValue === 7 && !isQspi) || (nextBitCounterValue === 4 && isQspi)) {
		isMosiFull := False
	}

	when(isStartOfTransaction && (io.transaction.command.payload.writeCount =/= 0 || io.transaction.command.payload.readCount =/= 0)) {
		// TODO: JUST MAKE A SINGLE REGISTER WITH THE DATA BEING THE ENTIRE PAYLOAD...BUT THIS CAN BE A REFACTORING AFTER THE FUNCTIONALITY HAS BEEN WRITTEN
		writeCountRemaining := io.transaction.command.payload.writeCount
		readCountRemaining := io.transaction.command.payload.readCount
		hasMoreMosi := io.transaction.mosi.valid
		isQspi := io.transaction.command.isQspi
		isIo0MosiTristated := io.transaction.command.isQspi && io.transaction.command.payload.writeCount === 0
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
		isIo0MosiTristated := isQspi
	}

	when(bitCounterWillIncrement) {
		bitCounter := nextBitCounterValue
	}

	clockDomain.withRevertedClockEdge() {
		val mosiOutBit = Reg(Bool()) init(True)
		switch(isQspi ## bitCounter) {
			for (i <- 0 to 7) {
				is(i) {
					mosiOutBit := mosi(7 - i)
				}
			}

			for (i <- Seq(8, 12)) {
				is(i) {
					mosiOutBit := mosi(12 - i)
				}
			}

			default {
				mosiOutBit := True
			}
		}

		io.pins.io0Mosi.outValue := !io.pins.clockEnable || mosiOutBit
	}

	io.pins.clockEnable := bitCounterWillIncrement
	io.pins.io0Mosi.isTristated := isIo0MosiTristated
	io.pins.io1Miso.outValue := True
	io.pins.io1Miso.isTristated := False
	io.pins.io2_Wp.outValue := False
	io.pins.io2_Wp.isTristated := False
	io.pins.io3_Hold.outValue := True
	io.pins.io3_Hold.isTristated := False

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
