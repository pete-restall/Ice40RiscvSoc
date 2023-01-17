package uk.co.lophtware.msfreference.tests.bus

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks

import uk.co.lophtware.msfreference.bus.MultiMasterSingleSlaveArbiter
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture

class MultiMasterSingleSlaveArbiterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	private val lessThanOneNumberOfMasters = Seq(0, -1, -2, -13).asTable("numberOfMasters")

	"MultiMasterSingleSlaveArbiter" must "not accept less than 1 master" in spinalContext { () =>
		forAll(lessThanOneNumberOfMasters) { (numberOfMasters: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy new MultiMasterSingleSlaveArbiter(numberOfMasters)
			thrown.getMessage must include("arg=numberOfMasters")
		}
	}

	private val numberOfMasters = Seq(1, 2, 3, 4, anyNumberOfMasters()).asTable("numberOfMasters")

	private def anyNumberOfMasters() = Random.between(1, 64)

	it must "have encoder IO for the number of masters passed to the constructor" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new MultiMasterSingleSlaveArbiter(numberOfMasters)
			arbiter.io.encoder.inputs.length must be(numberOfMasters)
		}}
	}

	it must "have the same number of bits in the grantedMasterIndex as the encoder output" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new MultiMasterSingleSlaveArbiter(numberOfMasters)
			arbiter.io.grantedMasterIndex.getWidth must be(arbiter.io.encoder.output.getWidth)
		}}
	}

	it must "have IO for the number of masters passed to the constructor" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new MultiMasterSingleSlaveArbiter(numberOfMasters)
			arbiter.io.masters.length must be(numberOfMasters)
		}}
	}
}
