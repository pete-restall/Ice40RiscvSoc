package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.Inspectors
import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusCrossbarMultiplexer
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.bus.CrossbarArbiterTestDoubles
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture

class WishboneBusCrossbarMultiplexerTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusCrossbarMultiplexer" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val arbiter = new WishboneBusCrossbarMultiplexer(dummyBusConfig(), anyNumberOfMasters(), anyNumberOfSlaves())
		arbiter.io.name must be("")
	}

	private def dummyBusConfig() = WishboneConfigTestDoubles.dummy()

	private def anyNumberOfMasters() = Random.between(1, 64)

	private def anyNumberOfSlaves() = Random.between(1, 64)

	it must "not accept null bus configuration" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusCrossbarMultiplexer(null, anyNumberOfMasters(), anyNumberOfSlaves())
		thrown.getMessage must include("arg=busConfig")
	}

	private val lessThanOne = Seq(0, -1, -2, -13).asTable("lessThanOne")

	it must "not accept less than 1 master" in spinalContext { () =>
		forAll(lessThanOne) { (numberOfMasters: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy new WishboneBusCrossbarMultiplexer(dummyBusConfig(), numberOfMasters, anyNumberOfSlaves())
			thrown.getMessage must include("arg=numberOfMasters")
		}
	}

	it must "not accept less than 1 slave" in spinalContext { () =>
		forAll(lessThanOne) { (numberOfSlaves: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy new WishboneBusCrossbarMultiplexer(dummyBusConfig(), anyNumberOfMasters(), numberOfSlaves)
			thrown.getMessage must include("arg=numberOfSlaves")
		}
	}

	private val numberOfMasters = Seq(1, 2, 3, 4, anyNumberOfMasters()).asTable("numberOfMasters")

	it must "have IO for the number of masters passed to the constructor" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val multiplexer = new WishboneBusCrossbarMultiplexer(dummyBusConfig(), numberOfMasters, anyNumberOfSlaves())
			multiplexer.io.masters.length must be(numberOfMasters)
		}
	}

	private val numberOfSlaves = Seq(1, 2, 3, 4, anyNumberOfSlaves()).asTable("numberOfSlaves")

	it must "have IO for the number of slaves passed to the constructor" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) =>
			val multiplexer = new WishboneBusCrossbarMultiplexer(dummyBusConfig(), anyNumberOfMasters(), numberOfSlaves)
			multiplexer.io.slaves.length must be(numberOfSlaves)
		}
	}

	"WishboneBusCrossbarMultiplexer masters" must "all use the bus configuration passed to the constructor" in spinalContext { () =>
		val busConfig = dummyBusConfig()
		val multiplexer = new WishboneBusCrossbarMultiplexer(busConfig, numberOfMasters=3, numberOfSlaves=anyNumberOfSlaves())
		forAll(multiplexer.io.masters) { master => master.wishbone.config must be(busConfig) }
	}

	private val slavesVsIndexWidth = Seq(
		(1, 1 bit),
		(2, 1 bit),
		(3, 2 bits),
		(4, 2 bits),
		(5, 3 bits),
		(6, 3 bits),
		(7, 3 bits),
		(8, 3 bits),
		(9, 4 bits),
		(16, 4 bits),
		(17, 5 bits),
		(128, 7 bits)
	)

	they must "all have a requestedSlaveIndex wide enough for the number of slaves" in spinalContext { () =>
		forAll(slavesVsIndexWidth.asTable("numberOfSlaves", "indexWidth")) { (numberOfSlaves: Int, indexWidth: BitCount) =>
			val multiplexer = new WishboneBusCrossbarMultiplexer(dummyBusConfig(), anyNumberOfMasters(), numberOfSlaves)
			forAll(multiplexer.io.masters) { master => master.requestedSlaveIndex.getWidth must be(indexWidth.value) }
		}
	}

	"WishboneBusCrossbarMultiplexer slaves" must "all use the bus configuration passed to the constructor" in spinalContext { () =>
		val busConfig = dummyBusConfig()
		val multiplexer = new WishboneBusCrossbarMultiplexer(busConfig, anyNumberOfMasters(), numberOfSlaves=3)
		forAll(multiplexer.io.slaves) { slave => slave.wishbone.config must be(busConfig) }
	}

	private val mastersVsIndexWidth = slavesVsIndexWidth

	they must "all have a grantedMasterIndex wide enough for the number of masters" in spinalContext { () =>
		forAll(mastersVsIndexWidth.asTable("numberOfMasters", "indexWidth")) { (numberOfMasters: Int, indexWidth: BitCount) =>
			val multiplexer = new WishboneBusCrossbarMultiplexer(dummyBusConfig(), numberOfMasters, anyNumberOfSlaves())
			forAll(multiplexer.io.slaves) { slave => slave.grantedMasterIndex.getWidth must be(indexWidth.value) }
		}
	}

	"WishboneBusCrossbarMultiplexer companion's apply() method" must "not accept a null busMap" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarMultiplexer(null, dummyArbiter())
		thrown.getMessage must include("arg=busMap")
	}

	private def dummyArbiter() = CrossbarArbiterTestDoubles.dummy()

	it must "not accept a null arbiter" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarMultiplexer(dummyBusMap(), null)
		thrown.getMessage must include("arg=arbiter")
	}

	private def dummyBusMap() = WishboneBusMasterSlaveMapTestDoubles.dummy()
}
