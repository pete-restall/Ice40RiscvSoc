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
	isMosiFull := io.transaction.mosi.valid // TODO: obviously test the next clock edge (when mosi.valid is set to false) as this should be full for the duration of the write (ie. write a test to ensure we only sample this at the start of the transaction)

	private val hasMoreMosi = Reg(Bool()) init(False)
	private val hasMoreMiso = Reg(Bool()) init(False)
	private val bitCounter = Reg(UInt(3 bits)) init(0)

	private val bitCounterWillIncrement = Bool()
	bitCounterWillIncrement := (writeCountRemaining =/= 0 && hasMoreMosi) || (writeCountRemaining === 0 && readCountRemaining =/= 0 && hasMoreMiso)

	private val isStartOfTransaction = Bool()
	isStartOfTransaction := writeCountRemaining === 0 && readCountRemaining === 0 && io.transaction.command.valid

	private val nextBitCounterValue = UInt(3 bits)
	nextBitCounterValue := bitCounter + (isQspi ? U(4) | U(1))

	private val bitCounterWillOverflowIfIncremented = Bool()
	bitCounterWillOverflowIfIncremented := nextBitCounterValue === 0

	when(isStartOfTransaction) {
		writeCountRemaining := io.transaction.command.payload.writeCount
		readCountRemaining := io.transaction.command.payload.readCount
		hasMoreMosi := io.transaction.mosi.valid
		hasMoreMiso := True//io.transaction.miso.ready // TODO: THIS IS INVALID; IF THE LAST BYTE OF THE PREVIOUS TRANSACTION HAS NOT BEEN CONSUMED THEN WE SHOULD NOT ALLOW THE TRANSACTION TO START
		isQspi := io.transaction.command.isQspi
	}

	when(bitCounterWillOverflowIfIncremented && writeCountRemaining =/= 0) {
		writeCountRemaining := writeCountRemaining - 1
		hasMoreMosi := io.transaction.mosi.valid
	}

	when(bitCounterWillOverflowIfIncremented && writeCountRemaining === 0) {
		readCountRemaining := readCountRemaining - 1
		hasMoreMiso := io.transaction.miso.ready
	}

	when(bitCounterWillIncrement) {
		bitCounter := nextBitCounterValue
	}

	io.pins.clockEnable := bitCounterWillIncrement// and not disable until everything clocked out, ie. if mosi becomes invalid during the transaction we need to have saved the mosi byte so can continue shifting it out (this allows pipelining); this also applies to readCount and writeCount changes...
	io.pins.io0Mosi.outValue := True
	io.pins.io0Mosi.isTristated := False
	io.pins.io1Miso.outValue := True
	io.pins.io1Miso.isTristated := False
	io.pins.io2_Wp.outValue := False
	io.pins.io2_Wp.isTristated := False
	io.pins.io3_Hold.outValue := True
	io.pins.io3_Hold.isTristated := False

	io.transaction.miso.payload := 0
	io.transaction.miso.valid := False
	io.transaction.mosi.ready := !clockDomain.isResetActive && !isMosiFull // need to discriminate between a write transaction and a read transaction...but needs to be a register (and test for this) because if mosi.valid is made false mid-transaction we still need to keep ready low
	io.transaction.command.ready := !clockDomain.isResetActive && !bitCounterWillIncrement // this should be fine because the clock will be enabled for any read or write transaction...verify that the tests cover this
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
