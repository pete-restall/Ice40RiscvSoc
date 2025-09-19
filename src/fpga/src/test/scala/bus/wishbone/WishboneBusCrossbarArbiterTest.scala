package net.restall.ice40riscvsoc.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MasterSlaveMap}
import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusCrossbarArbiter
import net.restall.ice40riscvsoc.multiplexing.{Encoder, SimpleEncoder}
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation.NonSimulationFixture

class WishboneBusCrossbarArbiterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	"WishboneBusCrossbarArbiter companion's apply(busMap) method" must "not use the 'io' prefix for signals" in spinalContext {
		val arbiter = WishboneBusCrossbarArbiter(dummyBusMap())
		arbiter.io.name must be("")
	}

	private def dummyBusMap() = WishboneBusMasterSlaveMapTestDoubles.dummy()

	it must "not accept a null busMap" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(null)
		thrown.getMessage must include("arg=busMap")
	}

	private val numberOfSlaves = Seq(1, 2, 3, 10).asTable("numberOfSlaves")

	it must "have IO for the number of slaves passed in the bus map" in spinalContext {
		forAll(numberOfSlaves) { (numberOfSlaves: Int) =>
			val arbiter = WishboneBusCrossbarArbiter(stubBusMapWith(numberOfSlaves=numberOfSlaves))
			arbiter.io.slaves.length must be(numberOfSlaves)
		}
	}

	private def stubBusMapWith(numberOfMasters: Int=1, numberOfSlaves: Int=1) = WishboneBusMasterSlaveMapTestDoubles.stubWith(numberOfMasters, numberOfSlaves)

	private val numberOfMasters = Seq(1, 2, 3, 10).asTable("numberOfMasters")

	it must "have IO for the number of masters passed in the bus map" in spinalContext {
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val arbiter = WishboneBusCrossbarArbiter(stubBusMapWith(numberOfMasters=numberOfMasters))
			arbiter.io.slaves.head.masters.length must be(numberOfMasters)
		}
	}

	it must "have CrossbarArbiter IO" in spinalContext {
		val arbiter = WishboneBusCrossbarArbiter(dummyBusMap())
		arbiter.io must be(a [CrossbarArbiter.IoBundle]);
	}

	"WishboneBusCrossbarArbiter companion's apply(busMap, encoderFactory) method" must "not accept a null busMap" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(null.asInstanceOf[MasterSlaveMap[Wishbone]], dummyEncoderFactory)
		thrown.getMessage must (include("arg=busMap") and include("null"))
	}

	private def dummyEncoderFactory(inputs: Vec[Bool]) = new SimpleEncoder(inputs.length).io

	it must "not accept a null encoderFactory" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(dummyBusMap(), null.asInstanceOf[Vec[Bool] => Encoder.IoBundle])
		thrown.getMessage must (include("arg=encoderFactory") and include("null"))
	}

	it must "have IO for the number of slaves passed in the bus map" in spinalContext {
		forAll(numberOfSlaves) { (numberOfSlaves: Int) =>
			val arbiter = WishboneBusCrossbarArbiter(stubBusMapWith(numberOfSlaves=numberOfSlaves), dummyEncoderFactory)
			arbiter.io.slaves.length must be(numberOfSlaves)
		}
	}

	it must "have IO for the number of masters passed in the bus map" in spinalContext {
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val arbiter = WishboneBusCrossbarArbiter(stubBusMapWith(numberOfMasters=numberOfMasters), dummyEncoderFactory)
			arbiter.io.slaves.head.masters.length must be(numberOfMasters)
		}
	}

	it must "have CrossbarArbiter IO" in spinalContext {
		val arbiter = WishboneBusCrossbarArbiter(dummyBusMap(), dummyEncoderFactory)
		arbiter.io must be(a [CrossbarArbiter.IoBundle]);
	}
}
