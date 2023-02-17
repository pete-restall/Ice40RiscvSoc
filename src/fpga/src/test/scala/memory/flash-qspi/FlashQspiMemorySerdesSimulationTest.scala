package uk.co.lophtware.msfreference.tests.memory.flashqspi

import scala.util.Random

import org.scalatest.AppendedClues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core.sim._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemorySerdes
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.TristatePinMatchers
import uk.co.lophtware.msfreference.tests.simulation.LightweightSimulationFixture

class FlashQspiMemorySerdesSimulationTest extends AnyFlatSpec
	with LightweightSimulationFixture[FlashQspiMemorySerdesFixture]
	with TableDrivenPropertyChecks
	with TristatePinMatchers {

	protected override def dutFactory() = new FlashQspiMemorySerdesFixture()

	"FlashQspiSerdes" must "hold the transaction command's ready flag low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.command.ready.toBoolean must be(false)
	}

	it must "set the transaction ready flag after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.command.ready.toBoolean must be(true)
	}

	it must "hold the transaction's MOSI ready flag low during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.transaction.mosi.ready.toBoolean must be(false)
	}

	it must "set the transaction's MOSI ready flag after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.transaction.mosi.ready.toBoolean must be(true)
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

	"FlashQspiSerdes pin controller" must "hold the IO0 (MOSI) pin high during reset" in simulator { fixture =>
		fixture.holdInReset()
		fixture.io.pins.io0Mosi must have(nonTristatedOutValueOf(true))
	}

	it must "hold the IO0 (MOSI) pin high after reset" in simulator { fixture =>
		fixture.reset()
		fixture.io.pins.io0Mosi must have(nonTristatedOutValueOf(true))
	}

	it must "hold the IO1 (MISO) pin high during reset" in simulator { fixture =>
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

	it must "be low when a write transaction is received without a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubInvalidMosi()
			fixture.clock()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be low when a transaction is received with a MOSI byte but without a write count" in simulator { fixture =>
		forAll(booleans.asTable("isQspi")) { isQspi =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=0)
			fixture.stubMosi(anyByte())
			fixture.clock()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "not rise asynchronously when a write transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			sleep(1)
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	private def anyByte() = Random.nextInt(1 << 8)

	it must "be high when a write transaction is received with a MOSI byte" in simulator { fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(true)
		}
	}

	it must "be high for (writeCount * 8 bits) SPI clocks or (writeCount * 2) QSPI clocks for a write transaction with " +
		"no read cycle and no MOSI stalls" in simulator { implicit fixture =>

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
		val clocksPerByte = if (isQspi) 2 else 8
		for (i <- 0 until clocksPerByte * numberOfBytes) {
			fixture.clockInactive()
			fixture.io.pins.clockEnable.toBoolean must be(true) withClue s"at clock ${i}"
			fixture.clockActive()
		}

		fixture.io.pins.clockEnable.toBoolean must be(false) withClue "at the end of bit-clocking"
	}

	it must "be high for 8 SPI clocks or 2 QSPI clocks for a write transaction with no read cycle and a MOSI stall" in simulator { implicit fixture =>
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

	it must "not rise asynchronously when a read transaction is received" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			sleep(1)
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be high on the active edge when a read transaction is received" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(true)
		}
	}

	it must "be low when a read transaction is received and the payload is not valid" in simulator { fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			fixture.clockActive()
			fixture.io.pins.clockEnable.toBoolean must be(false)
		}
	}

	it must "be high for (readCount * 8 bits) SPI clocks or (readCount * 2) QSPI clocks for a read transaction with " +
		"no write cycle and no MISO stalls" in simulator { implicit fixture =>

		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubInvalidCommand(isQspi, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	it must "be high for 8 SPI clocks or 2 QSPI clocks for a read transaction with no write cycle and a MISO stall" in simulator { implicit fixture =>
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

	"FlashQspiSerdes" must "register the byte count for write transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubCommand(isQspi, writeCount=writeCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi=isQspi)
		}
	}

	it must "register the I/O width for write transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(writeCountsVsTransactionTypes) { (writeCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, writeCount=writeCount)
			fixture.stubMosi(anyByte())
			fixture.clockActive()
			fixture.stubCommand(!isQspi, writeCount=writeCount)
			clockMustBeEnabledForExact(numberOfBytes=writeCount, isQspi=isQspi)
		}
	}

	it must "register the byte count for read transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(isQspi, readCount=readCount - 1)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	it must "register the I/O width for read transactions so they can be pipelined" in simulator { implicit fixture =>
		forAll(readCountsVsTransactionTypes) { (readCount, isQspi) =>
			fixture.reset()
			fixture.stubCommand(isQspi, readCount=readCount)
			fixture.stubReadyMiso()
			fixture.clockActive()
			fixture.stubCommand(!isQspi, readCount=readCount)
			clockMustBeEnabledForExact(numberOfBytes=readCount, isQspi=isQspi)
		}
	}

	// TODO: MAKE SURE A WRITE-READ TRANSACTION DOESN'T TURN INTO A WRITE-WRITE TRANSACTION IF THE COMMAND IS CHANGED AFTER THE INITIAL WRITE BUT BEFORE THE SECOND BYTE READ

	// TODO: TEST THE SCENARIO WHERE THE LAST MISO BYTE IS NOT READ AND A NEW READ TRANSACTION IS STARTED; THE CLOCK SHOULD NOT BE ENABLED SINCE THE BUFFER IS FULL

	// TODO: READ TRANSACTIONS
	// TODO: MIXED READ-WRITE TRANSACTIONS
	// TODO: MULTIPLE (PIPELINED) TRANSACTIONS

	// TODO: MUST HAVE mosi.ready WHEN READING AND NO MOSI BYTE RECEIVED
	// TODO: MUST HAVE !mosi.ready WHEN READING AND MOSI BYTE RECEIVED


	// TODO: MUST NOT MODIFY THE VALUES OF ANY PINS WHEN A WRITE TRANSACTION IS SUBMITTED WITHOUT A MOSI BYTE
	// TODO: MUST NOT MODIFY THE TRISTATES OF ANY PINS WHEN A WRITE TRANSACTION IS SUBMITTED WITHOUT A MOSI BYTE

	// TODO: MUST NOT MODIFY THE VALUES OF ANY PINS WHEN A TRANSACTION OF 0 READ AND 0 WRITE IS SUBMITTED
	// TODO: MUST NOT MODIFY THE TRISTATES OF ANY PINS WHEN A TRANSACTION OF 0 READ AND 0 WRITE IS SUBMITTED
}
