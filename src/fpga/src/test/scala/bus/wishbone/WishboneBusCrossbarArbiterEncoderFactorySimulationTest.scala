package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.{Inspectors, Suite}
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusCrossbarArbiter
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusCrossbarArbiterEncoderFactorySimulationTest(numberOfMasters: Int, numberOfSlaves: Int) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusCrossbarArbiterEncoderFactoryFixture]
	with Inspectors {

	protected override def dutFactory() = new WishboneBusCrossbarArbiterEncoderFactoryFixture(numberOfMasters, numberOfSlaves)

	"WishboneBusCrossbarArbiter companion's apply(busMap, encoders) method" must "not accept a null return value from the encoder factory" in simulator { fixture =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusCrossbarArbiter(dummyBusMap(), _ => null)
		thrown.getMessage must (include("arg=encoderFactory") and include("null"))
	}

	private def dummyBusMap() = WishboneBusMasterSlaveMapTestDoubles.dummy()

	private val combinationsOfMastersAndSlaves = for (m <- 0 until numberOfMasters; s <- 0 until numberOfSlaves) yield (m, s)

	it must "wire each slave's selector to the corresponding encoder's input" in simulator { fixture =>
		forAll(combinationsOfMastersAndSlaves) { case (masterIndex, slaveIndex) =>
			fixture.resetAllArbiterRequests()
			fixture.setArbiterRequest(masterIndex, slaveIndex)
			asLong(fixture.io.encoders(slaveIndex).inputs) must be(1l << masterIndex)
		}
	}

	private def asLong(values: Vec[Bool]) = values.zipWithIndex.foldLeft(0l)((acc, x) => acc | (if (x._1.toBoolean) 1l << x._2 else 0))

	it must "wire each encoder's output to the corresponding slave" in simulator { fixture =>
		forAll(combinationsOfMastersAndSlaves) { case (masterIndex, slaveIndex) =>
			fixture.io.encoders(slaveIndex).output #= masterIndex
			sleep(1)
			fixture.io.arbiterEncoders(slaveIndex).output.toInt must be(masterIndex)
		}
	}

	it must "wire each encoder's isValid to the corresponding slave" in simulator { fixture =>
		forAll(for (s <- 0 until numberOfSlaves; v <- Seq(true, false)) yield (s, v)) { case (slaveIndex, isValid) =>
			fixture.io.encoders(slaveIndex).isValid #= isValid
			sleep(1)
			fixture.io.arbiterEncoders(slaveIndex).isValid.toBoolean must be(isValid)
		}
	}
}
