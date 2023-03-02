package uk.co.lophtware.msfreference.tests.memory.flashqspi

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemorySerdes
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture

class FlashQspiMemorySerdesTest extends AnyFlatSpec with NonSimulationFixture {
	"FlashQspiSerdes" must "not use the 'io' prefix for signals" in spinalContext {
		val serdes = new FlashQspiMemorySerdes()
		serdes.io.name must be("")
	}

	it must "drive the transactional MISO as a master" in spinalContext {
		val serdes = new FlashQspiMemorySerdes()
		serdes.io.transaction.miso.isMasterInterface must be(true)
	}

	it must "have the transactional MOSI driven as a slave" in spinalContext {
		val serdes = new FlashQspiMemorySerdes()
		serdes.io.transaction.mosi.isMasterInterface must be(false)
	}

	it must "have the transaction command driven as a slave" in spinalContext {
		val serdes = new FlashQspiMemorySerdes()
		serdes.io.transaction.command.isMasterInterface must be(false)
	}
}
