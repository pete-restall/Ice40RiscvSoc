package uk.co.lophtware.msfreference.tests.memory.flashqspi

import scala.util.Random

import org.scalatest.AppendedClues._
import org.scalatest.matchers.must.Matchers._
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

	def anyNumberOfClocks(): Unit = {
		doForNumberOfClocks(Random.nextInt(10)) { }
	}

	def doForNumberOfClocks(numberOfClocks: Int)(afterClock: => Unit): Unit = {
		(0 until numberOfClocks).foreach { _ =>
			clock()
			afterClock
		}
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

	def atLeastOneClock(): Unit = {
		(0 to Random.between(1, 10)).foreach { _ => clock() }
	}

	def clockWhileEnabled(): Unit = clockWhile(io => io.pins.clockEnable.toBoolean)

	def clockWhile(predicate: FlashQspiMemorySerdes.IoBundle => Boolean): Unit = doClockWhile(predicate) { }

	def doClockWhile(predicate: FlashQspiMemorySerdes.IoBundle => Boolean)(afterClock: => Unit): Unit = {
		var timeout = 1000
		do {
			clock()
			afterClock
			timeout -= 1
		} while (predicate(io) && timeout > 0)
		timeout mustNot be(0) withClue("because of a timeout whilst clocking and waiting for !predicate")
	}

	def doWhileClockEnabled(afterClock: => Unit): Unit = doClockWhile(io => io.pins.clockEnable.toBoolean) { afterClock }

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

	def shiftMosiByte(isQspi: Boolean): Int = if (!isQspi) shiftSpiMosiByte() else shiftQspiMosiByte()

	def shiftSpiMosiByte(): Int = {
		var shiftedMosi = 0
		for (bit <- 7 to 0 by -1) {
			clock()
			shiftedMosi = (shiftedMosi << 1) | currentSpiMosiBit
		}

		shiftedMosi
	}

	def currentSpiMosiBit: Int = if (io.pins.io0Mosi.outValue.toBoolean) 1 else 0

	def shiftQspiMosiByte(): Int = {
		var shiftedMosi = 0
		for (cycle <- 0 to 1) {
			clock()
			shiftedMosi = (shiftedMosi << 4) | currentQspiMosiNybble
		}

		shiftedMosi
	}

	def currentQspiMosiNybble: Int =
		(if (io.pins.io0Mosi.outValue.toBoolean) 1 else 0) |
		(if (io.pins.io1Miso.outValue.toBoolean) 2 else 0) |
		(if (io.pins.io2_Wp.outValue.toBoolean) 4 else 0) |
		(if (io.pins.io3_Hold.outValue.toBoolean) 8 else 0)

	def stubReadyMiso(): Unit = {
		io.transaction.miso.ready #= true
	}

	def stubStalledMiso(): Unit = {
		io.transaction.miso.ready #= false
	}

	def shiftMisoByte(isQspi: Boolean, byte: Int): Unit = if (!isQspi) shiftSpiMisoByte(byte) else shiftQspiMisoByte(byte)

	def shiftSpiMisoByte(byte: Int): Unit = {
		for (bit <- 7 to 0 by -1) {
			setCurrentSpiMisoBit((byte & (1 << bit)) != 0)
			clock()
		}
	}

	def setCurrentSpiMisoBit(bit: Boolean): Unit = io.pins.io1Miso.inValue #= bit

	def shiftQspiMisoByte(byte: Int): Unit = {
		for (nybble <- 4 to 0 by -4) {
			setCurrentQspiMisoNybble(byte >> nybble)
			clock()
		}
	}

	def setCurrentQspiMisoNybble(nybble: Int): Unit = {
		io.pins.io0Mosi.inValue #= (nybble & 1) != 0
		io.pins.io1Miso.inValue #= (nybble & 2) != 0
		io.pins.io2_Wp.inValue #= (nybble & 4) != 0
		io.pins.io3_Hold.inValue #= (nybble & 8) != 0
	}
}
