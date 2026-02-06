package net.restall.ice40riscvsoc.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusCrossbarArbiter
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusCrossbarArbiterEncoderFactoryTest extends AnyFlatSpec with NonSimulationFixture {
	"WishboneBusCrossbarArbiter companion's apply(busMap, encoders) method" must "not accept a null return value from the encoder factory" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(dummyBusMap(), _ => null)
		thrown.getMessage must (include("arg=encoderFactory") and include("null"))
	}

	private def dummyBusMap() = WishboneBusMasterSlaveMapTestDoubles.dummy()
}
