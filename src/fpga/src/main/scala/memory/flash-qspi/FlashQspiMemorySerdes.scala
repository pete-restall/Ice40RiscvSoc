package uk.co.lophtware.msfreference.memory.flashqspi

import spinal.core._
import spinal.lib.{master, slave, Stream}

import uk.co.lophtware.msfreference.pins.TristatePin

class FlashQspiMemorySerdes extends Component {
	val io = new FlashQspiMemorySerdes.IoBundle()
	noIoPrefix()

	private val isStartOfTransaction = Bool()
	private val command = RegNextWhen(io.transaction.command.payload, isStartOfTransaction) init(FlashQspiMemorySerdes.emptyTransaction)
	isStartOfTransaction := command.writeCount === 0 && command.readCount === 0 && io.transaction.command.valid

	private val mosi = Reg(UInt(8 bits)) init(0)
	private val isMosiFull = Reg(Bool()) init(False)
	private val hasMoreMosi = Reg(Bool()) init(False)

	private val miso = Reg(UInt(8 bits)) init(0)
	private val isMisoFull = Reg(Bool()) init(False)
	private val isMisoValid = Reg(Bool()) init(False)

	private val bitCounter = Reg(UInt(3 bits)) init(0)
	private val bitCounterWillIncrement = Bool()
	bitCounterWillIncrement := (command.writeCount =/= 0 && hasMoreMosi) || (command.writeCount === 0 && command.readCount =/= 0 && !isMisoFull)

	private val nextBitCounterValue = UInt(3 bits)
	nextBitCounterValue := bitCounter + (command.isQspi ? U(4) | U(1))

	private val bitCounterWillOverflowIfIncremented = Bool()
	bitCounterWillOverflowIfIncremented := nextBitCounterValue === 0

	private val resetToDefaultSpi = RegNext(isStartOfTransaction && (io.transaction.command.payload.writeCount === 0 || io.transaction.command.payload.readCount === 0)) init(True)

	when(!isMosiFull && io.transaction.mosi.valid) {
		isMosiFull := True
		mosi := io.transaction.mosi.payload
		// TODO: this condition will need to toggle mosi.ready for one cycle
	}

	when((nextBitCounterValue === 7 && !command.isQspi) || (nextBitCounterValue === 4 && command.isQspi) && bitCounterWillIncrement) {
		isMosiFull := False
	}

	when(isStartOfTransaction) {
		hasMoreMosi := io.transaction.mosi.valid
	}

	when(bitCounterWillOverflowIfIncremented && command.writeCount =/= 0) {
		command.writeCount := command.writeCount - 1
		hasMoreMosi := io.transaction.mosi.valid
	}

	when(!bitCounterWillIncrement && command.writeCount =/= 0) {
		hasMoreMosi := io.transaction.mosi.valid // TODO: WHEN REFACTORING, SEE IF THIS REGISTER CAN BE REMOVED
	}

	when(bitCounterWillOverflowIfIncremented && command.writeCount === 0) {
		command.readCount := command.readCount - 1
		isMisoFull := !io.transaction.miso.ready
		isMisoValid := True
	}

	when(isMisoFull && io.transaction.miso.ready) {
		isMisoFull := False
	}

	when(isMisoValid && io.transaction.miso.ready) {
		isMisoValid := False
	}

	when(bitCounterWillIncrement) {
		bitCounter := nextBitCounterValue
	}

	when(bitCounterWillIncrement && !command.isQspi && command.writeCount === 0) {
		miso := (miso ## io.pins.io1Miso.inValue)(7 downto 0).asUInt
	}

	when(bitCounterWillIncrement && command.isQspi && command.writeCount === 0) {
		miso := (miso ## io.pins.io3_Hold.inValue ## io.pins.io2_Wp.inValue ## io.pins.io1Miso.inValue ## io.pins.io0Mosi.inValue)(7 downto 0).asUInt
	}

	clockDomain.withRevertedClockEdge() {
		val mosiOutBits = Reg(Bits(4 bits)) init(B("1011"))
		switch(command.isQspi ## bitCounter) {
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

		val isQspiRead = Bool()
		isQspiRead := (command.isQspi && command.writeCount === 0 && command.readCount =/= 0)

		val isIo0MosiTristated = Reg(Bool()) init(False)
		isIo0MosiTristated := isQspiRead || (!io.pins.clockEnable && isIo0MosiTristated && !resetToDefaultSpi)
		io.pins.io0Mosi.outValue := mosiOutBits(0)
		io.pins.io0Mosi.isTristated := isIo0MosiTristated

		val isIo1MisoTristated = Reg(Bool()) init(True)
		isIo1MisoTristated := isQspiRead || (!io.pins.clockEnable && isIo1MisoTristated && !resetToDefaultSpi) || !command.isQspi
		io.pins.io1Miso.outValue := mosiOutBits(1)
		io.pins.io1Miso.isTristated := isIo1MisoTristated

		val isIo2_WpTristated = Reg(Bool()) init(False)
		isIo2_WpTristated := isQspiRead || (!io.pins.clockEnable && isIo2_WpTristated && !resetToDefaultSpi)
		io.pins.io2_Wp.outValue := mosiOutBits(2)
		io.pins.io2_Wp.isTristated := isIo2_WpTristated

		val isIo3_HoldTristated = Reg(Bool()) init(False)
		isIo3_HoldTristated := isQspiRead || (!io.pins.clockEnable && isIo3_HoldTristated && !resetToDefaultSpi)
		io.pins.io3_Hold.outValue := mosiOutBits(3)
		io.pins.io3_Hold.isTristated := isIo3_HoldTristated
	}

	io.pins.clockEnable := bitCounterWillIncrement
	io.transaction.miso.payload := miso
	io.transaction.miso.valid := isMisoValid
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

	def emptyTransaction: Transaction = {
		var empty = Transaction().setAsDirectionLess()
		empty.isQspi := False
		empty.readCount := 0
		empty.writeCount := 0
		empty.freeze()
	}
}
