package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.bus.CrossbarArbiter
import uk.co.lophtware.msfreference.bus.wishbone.{WishboneBusCrossbarArbiter, WishboneBusMasterSlaveMap}
import uk.co.lophtware.msfreference.multiplexing.Encoder
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture

class WishboneBusCrossbarArbiterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	"WishboneBusCrossbarArbiter" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val arbiter = new WishboneBusCrossbarArbiter(dummyBusMap())
		arbiter.io.name must be("")
	}

	private def dummyBusMap() = WishboneBusMasterSlaveMapTestDoubles.dummy()

	it must "not accept a null busMap" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusCrossbarArbiter(null)
		thrown.getMessage must include("arg=busMap")
	}

	private val numberOfSlaves = Seq(1, 2, 3, 10).asTable("numberOfSlaves")

	it must "have IO for the number of slaves passed in the bus map" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) => {
			val arbiter = new WishboneBusCrossbarArbiter(stubBusMapWith(numberOfSlaves=numberOfSlaves))
			arbiter.io.slaves.length must be(numberOfSlaves)
		}}
	}

	private def stubBusMapWith(numberOfMasters: Int=1, numberOfSlaves: Int=1) = WishboneBusMasterSlaveMapTestDoubles.stubWith(numberOfMasters, numberOfSlaves)

	private val numberOfMasters = Seq(1, 2, 3, 10).asTable("numberOfMasters")

	it must "have IO for the number of masters passed in the bus map" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new WishboneBusCrossbarArbiter(stubBusMapWith(numberOfMasters=numberOfMasters))
			arbiter.io.slaves.head.masters.length must be(numberOfMasters)
		}}
	}

	it must "have CrossbarArbiter IO" in spinalContext { () =>
		val arbiter = new WishboneBusCrossbarArbiter(dummyBusMap())
		arbiter.io must be(a [CrossbarArbiter.IoBundle]);
	}

	"WishboneBusCrossbarArbiter companion's apply(busMap) method" must "not accept a null busMap" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(null.asInstanceOf[WishboneBusMasterSlaveMap])
		thrown.getMessage must (include("arg=busMap") and include("null"))
	}

	"WishboneBusCrossbarArbiter companion's apply(busMap, encoderFactory) method" must "not accept a null busMap" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(null.asInstanceOf[WishboneBusMasterSlaveMap], _ => null)
		thrown.getMessage must (include("arg=busMap") and include("null"))
	}

	it must "not accept a null encoderFactory" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(dummyBusMap(), null.asInstanceOf[Vec[Bool] => Encoder.IoBundle])
		thrown.getMessage must (include("arg=encoderFactory") and include("null"))
	}
}
