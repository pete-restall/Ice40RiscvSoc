package uk.co.lophtware.msfreference.tests.memory.flashqspi

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemorySerdes

class FlashQspiMemorySerdesFixture extends Component {
	val io = new FlashQspiMemorySerdes.IoBundle()

	private val dut = new FlashQspiMemorySerdes()
	io <> dut.io

	def reset(): Unit = {
		holdInReset()
		clockInactive()
		clockDomain.deassertReset()
		sleep(10)
	}

	def holdInReset(): Unit = {
		clockDomain.assertReset()
		sleep(10)
	}

	def clockInactive(): Unit = {
		sleep(5)
		clockDomain.fallingEdge()
		sleep(5)
	}

	def clock(): Unit = {
		clockActive()
		clockInactive()
	}

	def clockActive(): Unit = {
		sleep(5)
		clockDomain.risingEdge()
		sleep(5)
	}

	def stubCommand(isQspi: Boolean, writeCount: Int = 0, readCount: Int = 0): Unit = {
		stubInvalidCommand(isQspi, writeCount, readCount)
		io.transaction.command.valid #= true
	}

	def stubInvalidCommand(isQspi: Boolean, writeCount: Int = 0, readCount: Int = 0): Unit = {
		io.transaction.command.payload.isQspi #= isQspi
		io.transaction.command.payload.writeCount #= writeCount
		io.transaction.command.payload.readCount #= readCount
		io.transaction.command.valid #= false
	}

	def stubMosi(byte: Int): Unit = {
		stubInvalidMosi(byte)
		io.transaction.mosi.valid #= true
	}

	def stubInvalidMosi(byte: Int = 0): Unit = {
		io.transaction.mosi.payload #= byte
		io.transaction.mosi.valid #= false
	}

	def stubReadyMiso(): Unit = {
		io.transaction.miso.ready #= true
	}

	def stubStalledMiso(): Unit = {
		io.transaction.miso.ready #= false
	}
}
