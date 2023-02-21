package uk.co.lophtware.msfreference.tests.memory.flashqspi

import scala.util.Random

import org.scalatest.AppendedClues._
import org.scalatest.Inspectors
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core.sim._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemorySerdes
import uk.co.lophtware.msfreference.pins.TristatePin
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.TristatePinMatchers
import uk.co.lophtware.msfreference.tests.simulation.LightweightSimulationFixture

class FlashQspiMemorySerdesSimulationTest extends AnyFlatSpec
	with LightweightSimulationFixture[FlashQspiMemorySerdesFixture]
	with TableDrivenPropertyChecks
	with TristatePinMatchers
	with Inspectors {

	protected override def dutFactory() = new FlashQspiMemorySerdesFixture()

	"FlashQspiSerdes" must "hold the transaction command's ready flag low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.command.ready.toBoolean must be(false)
	}

	it must "set the transaction ready flag after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.command.ready.toBoolean must be(true)
	}

	it must "hold the transaction's MISO valid flag low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.miso.valid.toBoolean must be(false)
	}

	it must "hold the transaction's MISO valid flag low after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.miso.valid.toBoolean must be(false)
	}

	it must "zero the transaction's MISO payload during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.miso.payload.toInt must be(0)
	}

	it must "zero the transaction's MISO payload after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.miso.payload.toInt must be(0)
	}

	"FlashQspiSerdes pin controller" must "hold the IO1 (MISO) pin high during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.io1Miso must have(nonTristatedOutValueOf(true))
	}

	it must "hold the IO1 (MISO) pin high after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.io1Miso must have(nonTristatedOutValueOf(true))
	}

	it must "hold the IO2 (/WP) pin low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.io2_Wp must have(nonTristatedOutValueOf(false))
	}

	it must "hold the IO2 (/WP) pin low after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.io2_Wp must have(nonTristatedOutValueOf(false))
	}

	it must "hold the IO3 (/HOLD) pin high during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.io3_Hold must have(nonTristatedOutValueOf(true))
	}

	it must "hold the IO3 (/HOLD) pin high after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.io3_Hold must have(nonTristatedOutValueOf(true))
	}

	private val writeCounts = Seq(1, 2, 7, anyWriteCount()).asTable("writeCount")

	private def anyWriteCount() = Random.between(1, 8)

	private val booleans = Seq(false, true)

	private val writeCountsVsTransactionTypes = allCombinationsOf(writeCounts, booleans).asTable("writeCount", "isQspi")

	private def allCombinationsOf[A, B](a: Seq[A], b: Seq[B]) = for (x <- a; y <- b) yield (x, y)

	it must "not stall the MOSI stream when a write transaction is received without a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubInvalidMosi()
			fixture.clock()
			fixture.io.transaction.mosi.ready.toBoolean must be(true)
		}
	}

	it must "not stall the MOSI stream asynchronously when a write transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			sleep(1)
			fixture.io.transaction.mosi.ready.toBoolean must be(true)
		}
	}

	it must "still stall the MOSI stream when a transaction is received without a write count" in simulator { fixture =>
		forAll(booleans.asTable("isQspi")) { isQspi =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=0)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(false)
		}
	}

	it must "stall the MOSI stream on the active clock edge when a write transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(false)
		}
	}

	it must "stall the MOSI stream on the active clock edge when a MOSI byte is received without a transaction" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubInvalidCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(false)
		}
	}

	private val readCounts = Seq(1, 2, 7, anyReadCount()).asTable("readCount")

	private def anyReadCount() = Random.between(1, 8)

	private val readCountsVsTransactionTypes = allCombinationsOf(readCounts, booleans).asTable("readCount", "isQspi")

	it must "stall the command stream on the active clock edge when a read transaction is received" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.clockActive()
			fixture.io.transaction.command.ready.toBoolean must be(false)
		}
	}

	"FlashQspiSerdes clock enable" must "be held low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.clockEnable.toBoolean must be(false)
	}

	it must "be held low after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.clockEnable.toBoolean must be(false)
	}

	it must "be low when a write-only transaction is received without a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubInvalidMosi()
			fixture.clock()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be low when a transaction is received with a MOSI byte but without both read and write counts" in simulator { fixture =>
		forAll(booleans.asTable("isQspi")) { isQspi =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=0, readCount=0)
			fixture.stubMosi(anyByte())
			fixture.clock()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "not rise asynchronously when a write-only transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			sleep(1)
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	private def anyByte() = Random.nextInt(1 << 8)

	it must "be high when a write-only transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(true)
		}
	}

	it must "be high for (writeCount * 8 bits) SPI clocks or (writeCount * 2) QSPI clocks for a write-only transaction with no MOSI stalls" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, writeCount=writeCount)
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi=isQspi)
		}
	}

	private def clockMustBeEnabledForExact(numberOfBytes: Int, isQspi: Boolean)(implicit fixture: FlashQspiMemorySerdesFixture) = {
		clockMustBeEnabledFor(numberOfBytes, isQspi)
		fixture.io.pins.clockEnable.toBoolean must be(false) withClue "at the end of bit-clocking"
	}

	private def clockMustBeEnabledFor(numberOfBytes: Int, isQspi: Boolean)(implicit fixture: FlashQspiMemorySerdesFixture) = {
		val clocksPerByte = if (isQspi) 2 else 8
		forAll(0 until clocksPerByte * numberOfBytes) { i =>
			fixture.clockInactive()
			fixture.io.pins.clockEnable.toBoolean must be(true) withClue s"at clock ${i}"
			fixture.clockActive()
		}
	}

	it must "be high for 8 SPI clocks or 2 QSPI clocks for a write-only transaction with no read cycle and a MOSI stall" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, writeCount=writeCount)
			fixture.stubInvalidMosi()
			clockMustBeEnabledForExact(numberOfBytes=1, isQspi=isQspi)
		}
	}

	it must "not rise asynchronously when a read-only transaction is received" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			sleep(1)
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be high on the active edge when a read-only transaction is received" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(true)
		}
	}

	it must "be low when a read-only transaction is received and the payload is not valid" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be high for (readCount * 8 bits) SPI clocks or (readCount * 2) QSPI clocks for a read-only transaction with no MISO stalls" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	it must "be high for 8 SPI clocks or 2 QSPI clocks for a read-only transaction and a MISO stall" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			fixture.stubStalledMiso()
			clockMustBeEnabledForExact(numberOfBytes=1, isQspi=isQspi)
		}
	}

	it must "be low for a new read transaction if the last transaction's MISO byte is still stalled" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			stubFixtureForStalledLastMisoByte(readCount, isQspi)
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	private def stubFixtureForStalledLastMisoByte(readCount: Int, isQspi: Boolean)(implicit fixture: FlashQspiMemorySerdesFixture) = {
		fixture.reset()
		fixture.stubCommand(isQspi, readCount=readCount)
		fixture.stubReadyMiso()
		fixture.clockActive()
		fixture.stubInvalidCommand(isQspi, readCount=readCount)
		clockMustBeEnabledFor(numberOfBytes=readCount - 1, isQspi=isQspi)
		fixture.stubStalledMiso()
		clockMustBeEnabledForExact(numberOfBytes=1, isQspi=isQspi)
		fixture.stubCommand(isQspi, readCount=1)
		fixture.clockInactive()
	}

	it must "be high for a new write transaction if the last read transaction's MISO byte is still stalled" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			stubFixtureForStalledLastMisoByte(readCount=2, isQspi)
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(true)
		}
	}

	it must "be low for the read transaction following a write transaction if the last read transaction's MISO byte is still stalled" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			stubFixtureForStalledLastMisoByte(readCount=2, isQspi)
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=1)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi)
			fixture.clockInactive()
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	private val writeAndReadCountsVsTransactionTypes = allCombinationsOf(writeCounts, readCounts, booleans).asTable("writeCount", "readCount", "isQspi")

	private def allCombinationsOf[A, B, C](a: Seq[A], b: Seq[B], c: Seq[C]) = for (x <- a; y <- b; z <- c) yield (x, y, z)

	it must "be high for ((writeCount + readCount) * 8 bits) SPI clocks or ((writeCount + readCount) * 2) QSPI clocks for a write-read transaction " +
		"with no MOSI or MISO stalls" in simulator { implicit fixture =>

		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=writeCount + readCount, isQspi=isQspi)
		}
	}

	"FlashQspiSerdes" must "register the byte count for write-only transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubCommand(isQspi, writeCount=writeCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi=isQspi)
		}
	}

	it must "register the I/O width for write-only transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubCommand(!isQspi, writeCount=writeCount)
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi=isQspi)
		}
	}

	it must "register the byte count for read-only transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(isQspi, readCount=readCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	it must "register the I/O width for read-only transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(!isQspi, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	it must "register the byte read count for write-read transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(isQspi, readCount=readCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=writeCount + readCount, isQspi=isQspi)
		}
	}

	it must "register the byte write count for write-read transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(isQspi, writeCount=writeCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=writeCount + readCount, isQspi=isQspi)
		}
	}

	it must "register the I/O width for write-read transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(!isQspi, writeCount=writeCount, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=writeCount + readCount, isQspi=isQspi)
		}
	}

	"FlashQspiSerdes IO0 (MOSI) line" must "be held high during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.io0Mosi must have(nonTristatedOutValueOf(true))
	}

	it must "be held high after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.io0Mosi must have(nonTristatedOutValueOf(true))
	}

	it must "not be tristated on the first rising clock edge for (Q)SPI write-only transactions" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(false)
		}
	}

	private def stubIo0MosiAsTristate()(implicit fixture: FlashQspiMemorySerdesFixture) = {
		fixture.stubCommand(isQspi=true, readCount=1)
		fixture.stubReadyMiso()
		fixture.clockWhileEnabled()
		fixture.io.pins.io0Mosi.isTristated.toBoolean must be(true) withClue "because QSPI reads should tristate all I/O pins"
		fixture.io.pins.clockEnable.toBoolean must be(false) withClue "because QSPI read should have been completed"
	}

	it must "not be tristated on the first rising clock edge for (Q)SPI write-read transactions" in simulator { implicit fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(false)
		}
	}

	it must "not be tristated on the first rising clock edge for empty (Q)SPI transactions if not previously tristated" in simulator { implicit fixture =>
		forAll(booleans) { isQspi =>
			fixture.reset()
			stubIo0MosiAsNonTristate()
			fixture.stubCommand(isQspi, writeCount=0, readCount=0)
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(false)
		}
	}

	private def stubIo0MosiAsNonTristate()(implicit fixture: FlashQspiMemorySerdesFixture) = {
		fixture.stubCommand(isQspi=true, writeCount=1)
		fixture.stubMosi(anyByte())
		fixture.clockWhileEnabled()
		fixture.io.pins.io0Mosi.isTristated.toBoolean must be(false) withClue "because QSPI writes should drive all I/O pins"
		fixture.io.pins.clockEnable.toBoolean must be(false) withClue "because QSPI write should have been completed"
	}

	it must "be tristate on the first rising clock edge for empty Q(SPI) transactions if previously tristated" in simulator { implicit fixture =>
		forAll(booleans) { isQspi =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi, writeCount=0, readCount=0)
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(true)
		}
	}

	it must "not be tristated on the first rising clock edge for SPI read-only transactions" in simulator { implicit fixture =>
		forAll(readCounts) { readCount =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi=false, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(false)
		}
	}

	it must "be tristated on the first rising clock edge for QSPI read-only transactions" in simulator { implicit fixture =>
		forAll(readCounts) { readCount =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi=true, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.io.pins.io0Mosi.isTristated.toBoolean must be(true)
		}
	}

	it must "not be tristated on the first rising clock edge following the last written bit for (Q)SPI write-only transactions" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			var isTristateOnLastRisingEdge: Boolean = true
			do {
				fixture.clockActive()
				isTristateOnLastRisingEdge = fixture.io.pins.io0Mosi.isTristated.toBoolean
				fixture.clockInactive()
			} while (fixture.io.pins.clockEnable.toBoolean)
			isTristateOnLastRisingEdge must be(false)
		}
	}

	private val writeAndReadCounts = allCombinationsOf(writeCounts, readCounts).asTable("writeCount", "readCount")

	it must "not be tristated on the first rising clock edge following the last written bit for SPI write-read transactions" in simulator { implicit fixture =>
		forAll(writeAndReadCounts) { (writeCount, readCount) =>
			fixture.reset()
			stubIo0MosiAsTristate()
			fixture.stubCommand(isQspi=false, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubStalledMiso()
			var isTristateOnLastRisingEdge: Boolean = true
			do {
				fixture.clockActive()
				isTristateOnLastRisingEdge = fixture.io.pins.io0Mosi.isTristated.toBoolean
				fixture.clockInactive()
			} while (fixture.io.pins.clockEnable.toBoolean)
			isTristateOnLastRisingEdge must be(false)
		}
	}

	it must "be tristated on the first rising clock edge following the last written bit for QSPI write-read transactions" in simulator { implicit fixture =>
		forAll(writeAndReadCounts) { (writeCount, readCount) =>
			fixture.reset()
			stubIo0MosiAsNonTristate()
			fixture.stubCommand(isQspi=true, writeCount=writeCount, readCount=readCount)
			fixture.stubMosi(anyByte())
			fixture.stubStalledMiso()
			var isTristateOnLastRisingEdge: Boolean = false
			do {
				fixture.clockActive()
				isTristateOnLastRisingEdge = fixture.io.pins.io0Mosi.isTristated.toBoolean // TODO: EXAMINE THE VCD FOR THE TESTS TO MAKE SURE THAT THE SWITCH FROM DRIVEN TO TRISTATE IS DONE ON THE CORRECT EDGE
				fixture.clockInactive()
			} while (fixture.io.pins.clockEnable.toBoolean)
			isTristateOnLastRisingEdge must be(true)
		}
	}

	it must "be the MOSI byte shifted out on 8 falling clock edges, most significant bit first, for an SPI write-only transaction" in simulator { implicit fixture =>
		val transactionMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubMosi(transactionMosi)
		forAll(7 to 0 by -1) { bit =>
			mosiPinMustChangeOnFallingEdge(mosi=transactionMosi, bit=bit, pins => pins.io0Mosi)
		}
	}

	private def mosiPinMustChangeOnFallingEdge(mosi: Int, bit: Int, pinFrom: FlashQspiMemorySerdes.PinIoBundle => TristatePin.IoBundle)(implicit fixture: FlashQspiMemorySerdesFixture) = {
		val expectedBit = (mosi & (1 << bit)) != 0
		val previousBit = pinFrom(fixture.io.pins).outValue.toBoolean
		fixture.clockActive()
		pinFrom(fixture.io.pins) must have(nonTristatedOutValueOf(previousBit)) withClue s"for bit ${bit} (previous bit / rising clock)"
		fixture.clockInactive()
		pinFrom(fixture.io.pins) must have(nonTristatedOutValueOf(expectedBit)) withClue s"for bit ${bit} (next bit / falling clock)"
	}

	it must "be shifted from a registered MOSI byte for an SPI write-only transaction" in simulator { fixture =>
		val transactionMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubMosi(transactionMosi)
		var shiftedMosi = 0
		forAll(7 to 0 by -1) { bit =>
			fixture.clockActive()
			fixture.stubMosi(~transactionMosi & 0xff)
			fixture.clockInactive()
			shiftedMosi = (shiftedMosi << 1) | (if (fixture.io.pins.io0Mosi.outValue.toBoolean) 1 else 0)
		}

		shiftedMosi must be(transactionMosi)
	}

	it must "be shifted MOSI bytes for an SPI multi-byte write-only non-pipelined transaction" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=false, writeCount=writeCount)
			forAll(transactionMosis) { transactionMosi =>
				fixture.stubMosi(transactionMosi)
				fixture.shiftSpiMosiByte() must be(transactionMosi) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
			}
		}
	}

	it must "be shifted MOSI bytes for an SPI multi-byte write-only pipelined transaction" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=false, writeCount=writeCount)
			fixture.stubMosi(transactionMosis.head)
			var transactionMosi = transactionMosis.head
			forAll(transactionMosis.tail) { nextTransactionMosi =>
				var shiftedMosi = 0
				forAll(7 to 0 by -1) { bit =>
					fixture.clockActive()
					fixture.stubMosi(nextTransactionMosi)
					fixture.clockInactive()
					shiftedMosi = (shiftedMosi << 1) | (if (fixture.io.pins.io0Mosi.outValue.toBoolean) 1 else 0)
				}

				shiftedMosi must be(transactionMosi)
				transactionMosi = nextTransactionMosi
			}

			fixture.shiftSpiMosiByte() must be(transactionMosi) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
		}
	}

	it must "be shifted MOSI bytes for an SPI multi-byte write-only transaction when there are stalls" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=false, writeCount=writeCount)
			forAll(transactionMosis) { transactionMosi =>
				fixture.stubInvalidMosi(anyByteExcept(transactionMosi))
				fixture.anyNumberOfClocks()
				fixture.stubMosi(transactionMosi)
				fixture.shiftSpiMosiByte() must be(transactionMosi) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
			}
		}
	}

	private def anyByteExcept(byte: Int): Int = {
		val anotherByte = Random.nextInt(1 << 8)
		if (anotherByte != byte) anotherByte else anyByteExcept(byte)
	}

	it must "not be modified for an SPI write-only transaction if no MOSI byte has been registered" in simulator { fixture =>
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubInvalidMosi(0x00)
		fixture.shiftSpiMosiByte() must be(0xff) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
	}

	it must "not be modified for an SPI write-only transaction until a MOSI byte has been registered" in simulator { fixture =>
		val transactioMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubInvalidMosi(anyByteExcept(transactioMosi))
		fixture.atLeastOneClock()
		fixture.stubMosi(transactioMosi)
		fixture.shiftSpiMosiByte() must be(transactioMosi) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
	}

	it must "use the MOSI byte registered on the first rising edge of a transaction" in simulator { fixture =>
		val transactioMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubMosi(transactioMosi)
		fixture.clockActive()
		fixture.stubMosi(~transactioMosi & 0xff)
		fixture.shiftSpiMosiByte() must be(transactioMosi) // TODO: CAN BE GENERALISED FOR Q(SPI) ?  BUT ONLY WHEN ALL PINS ARE DONE...
	}
///////////////////////////////

	it must "be the bits 4 and 0 of the MOSI byte shifted out on 2 falling clock edges, for a QSPI write-only transaction" in simulator { implicit fixture =>
		forAll(qspiMosiBytesFor(bits=(4, 0)).asTable("transactionMosi")) { transactionMosi =>
			fixture.reset()
			fixture.stubCommand(isQspi=true, writeCount=1)
			fixture.stubMosi(transactionMosi)
			forAll(4 to 0 by -4) { bit =>
				mosiPinMustChangeOnFallingEdge(mosi=transactionMosi, bit=bit, pins => pins.io0Mosi)
			}
		}
	}

	private val qspiBitPairCombinations = Seq((0, 0), (0, 1), (1, 0), (1, 1))

	private def qspiMosiBytesFor(bits: (Int, Int)) = {
		val nybbleBits = (if (bits._1 > 3) bits._1 - 4 else bits._1, if (bits._2 > 3) bits._2 - 4 else bits._2)
		val upperNybbles = qspiBitPairCombinations.map(x => (0x0f & ~(1 << nybbleBits._1)) | (x._1 << nybbleBits._1))
		val lowerNybbles = qspiBitPairCombinations.map(x => (0x0f & ~(1 << nybbleBits._2)) | (x._2 << nybbleBits._2))
		upperNybbles.zip(lowerNybbles).map(x => (x._1 << 4) | x._2)
	}

	it must "be bits 4 and 0 shifted from a registered MOSI byte for a QSPI write-only transaction" in simulator { fixture =>
		forAll(qspiMosiBytesFor(bits=(4, 0)).asTable("transactionMosi")) { transactionMosi =>
			fixture.reset()
			fixture.stubCommand(isQspi=true, writeCount=1)
			fixture.stubMosi(transactionMosi)
			var shiftedMosi = transactionMosi & ~((1 << 4) | (1 << 0))
			forAll(4 to 0 by -4) { bit =>
				fixture.clockActive()
				fixture.stubMosi(~transactionMosi & 0xff)
				fixture.clockInactive()
				shiftedMosi |= (if (fixture.io.pins.io0Mosi.outValue.toBoolean) 1 << bit else 0)
			}

			shiftedMosi must be(transactionMosi)
		}
	}

/*
// TODO: CAN THESE BE GENERALISED INTO A SINGLE TEST FOR SPI / QSPI ?
	it must "be shifted MOSI bytes for a QSPI multi-byte write-only non-pipelined transaction" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=true, writeCount=writeCount)
			forAll(transactionMosis) { transactionMosi =>
				fixture.stubMosi(transactionMosi)
				fixture.shiftMosiByte(isQspi=true) must be(transactionMosi)
			}
		}
	}

	it must "be shifted MOSI bytes for an SPI multi-byte write-only pipelined transaction" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=false, writeCount=writeCount)
			fixture.stubMosi(transactionMosis.head)
			var transactionMosi = transactionMosis.head
			forAll(transactionMosis.tail) { nextTransactionMosi =>
				var shiftedMosi = 0
				forAll(7 to 0 by -1) { bit =>
					fixture.clockActive()
					fixture.stubMosi(nextTransactionMosi)
					fixture.clockInactive()
					shiftedMosi = (shiftedMosi << 1) | (if (fixture.io.pins.io0Mosi.outValue.toBoolean) 1 else 0)
				}

				shiftedMosi must be(transactionMosi)
				transactionMosi = nextTransactionMosi
			}

			fixture.shiftMosiByte() must be(transactionMosi)
		}
	}

	it must "be shifted MOSI bytes for an SPI multi-byte write-only transaction when there are stalls" in simulator { fixture =>
		forAll(writeCounts) { writeCount =>
			val transactionMosis = Array.fill(writeCount) { anyByte() }
			fixture.reset()
			fixture.stubCommand(isQspi=false, writeCount=writeCount)
			forAll(transactionMosis) { transactionMosi =>
				fixture.stubInvalidMosi(anyByteExcept(transactionMosi))
				fixture.anyNumberOfClocks()
				fixture.stubMosi(transactionMosi)
				fixture.shiftMosiByte() must be(transactionMosi)
			}
		}
	}

	it must "not be modified for an SPI write-only transaction if no MOSI byte has been registered" in simulator { fixture =>
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubInvalidMosi(0x00)
		fixture.shiftMosiByte() must be(0xff)
	}

	it must "not be modified for an SPI write-only transaction until a MOSI byte has been registered" in simulator { fixture =>
		val transactioMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubInvalidMosi(anyByteExcept(transactioMosi))
		fixture.atLeastOneClock()
		fixture.stubMosi(transactioMosi)
		fixture.shiftMosiByte() must be(transactioMosi)
	}

	it must "use the MOSI byte registered on the first rising edge of a transaction" in simulator { fixture =>
		val transactioMosi = anyByte()
		fixture.reset()
		fixture.stubCommand(isQspi=false, writeCount=1)
		fixture.stubMosi(transactioMosi)
		fixture.clockActive()
		fixture.stubMosi(~transactioMosi & 0xff)
		fixture.shiftMosiByte() must be(transactioMosi)
	}
*/
///////////////////////////////
	"FlashQspiSerdes transaction's MOSI ready flag" must "be held low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.mosi.ready.toBoolean must be(false)
	}

	it must "be held high after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.mosi.ready.toBoolean must be(true)
	}

	it must "remain high on the first clock when the transaction's MOSI byte is not valid" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubInvalidCommand(isQspi, writeCount, readCount)
			fixture.stubInvalidMosi()
			fixture.clock()
			fixture.io.transaction.mosi.ready.toBoolean must be(true)
		}
	}

	it must "fall low on the first rising clock edge when the transaction's MOSI byte is valid" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubInvalidCommand(isQspi, writeCount, readCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(false)
		}
	}

	it must "rise high on the last rising clock edge of the shifted bit clock for a non-pipelined transaction" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount, readCount)
			fixture.stubMosi(anyByte())
			val numberOfClocksPerByte = if (isQspi) 2 else 8
			forAll(0 until numberOfClocksPerByte - 1) { i =>
				fixture.clock()
				fixture.stubInvalidMosi()
				fixture.io.transaction.mosi.ready.toBoolean must be(false) withClue s"at clock ${i}"
			}

			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(true) withClue "at the end of bit-clocking"
		}
	}

	it must "rise high on the last rising clock edge of the shifted bit clock for a pipelined transaction" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount, readCount)
			fixture.stubMosi(anyByte())
			val numberOfClocksPerByte = if (isQspi) 2 else 8
			forAll(0 until numberOfClocksPerByte - 1) { i =>
				fixture.clock()
				fixture.io.transaction.mosi.ready.toBoolean must be(false) withClue s"at clock ${i}"
			}

			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(true) withClue "at the end of bit-clocking"
		}
	}

	it must "remain high on the first rising clock edge for a non-pipelined transaction" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount, readCount)
			fixture.stubMosi(anyByte())
			val numberOfClocksPerByte = if (isQspi) 2 else 8
			forAll(0 until numberOfClocksPerByte) { i =>
				fixture.clock()
				fixture.stubInvalidMosi()
			}

			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(true)
		}
	}

	it must "fall low on the first rising clock edge for a pipelined transaction" in simulator { fixture =>
		forAll(writeAndReadCountsVsTransactionTypes) { (writeCount, readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount, readCount)
			fixture.stubMosi(anyByte())
			val numberOfClocksPerByte = if (isQspi) 2 else 8
			forAll(0 until numberOfClocksPerByte) { i =>
				fixture.clock()
			}

			fixture.clockActive()
			fixture.io.transaction.mosi.ready.toBoolean must be(false)
		}
	}


	// TODO: MUST NOT MODIFY THE VALUES OF ANY PINS WHEN A WRITE TRANSACTION IS SUBMITTED WITHOUT A MOSI BYTE
	// TODO: MUST NOT MODIFY THE TRISTATES OF ANY PINS WHEN A WRITE TRANSACTION IS SUBMITTED WITHOUT A MOSI BYTE

	// TODO: MUST NOT MODIFY THE VALUES OF ANY PINS WHEN A TRANSACTION OF 0 READ AND 0 WRITE IS SUBMITTED
	// TODO: MUST NOT MODIFY THE TRISTATES OF ANY PINS WHEN A TRANSACTION OF 0 READ AND 0 WRITE IS SUBMITTED
}
