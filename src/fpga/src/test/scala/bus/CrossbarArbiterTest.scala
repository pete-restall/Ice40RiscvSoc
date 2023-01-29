package uk.co.lophtware.msfreference.tests.bus

import scala.util.Random

import org.scalatest.Inspectors
import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks

import uk.co.lophtware.msfreference.bus.CrossbarArbiter
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation.NonSimulationFixture

class CrossbarArbiterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"CrossbarArbiter" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val arbiter = new CrossbarArbiter(anyNumberOfMasters(), anyNumberOfSlaves())
		arbiter.io.name must be("")
	}

	private def anyNumberOfMasters() = Random.between(1, 64)

	private def anyNumberOfSlaves() = Random.between(1, 64)

	private val lessThanOne = Seq(0, -1, -2, -13).asTable("lessThanOne")

	it must "not accept less than 1 master" in spinalContext { () =>
		forAll(lessThanOne) { (numberOfMasters: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy new CrossbarArbiter(numberOfMasters, anyNumberOfSlaves())
			thrown.getMessage must include("arg=numberOfMasters")
		}
	}

	it must "not accept less than 1 slave" in spinalContext { () =>
		forAll(lessThanOne) { (numberOfSlaves: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy new CrossbarArbiter(anyNumberOfMasters(), numberOfSlaves)
			thrown.getMessage must include("arg=numberOfSlaves")
		}
	}

	private val numberOfSlaves = Seq(1, 2, 3, 4, anyNumberOfSlaves()).asTable("numberOfSlaves")

	it must "have IO for the number of slaves passed to the constructor" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) => {
			val arbiter = new CrossbarArbiter(anyNumberOfMasters(), numberOfSlaves)
			arbiter.io.slaves.length must be(numberOfSlaves)
		}}
	}

	private val numberOfMasters = Seq(1, 2, 3, 4, anyNumberOfMasters()).asTable("numberOfMasters")

	"CrossbarArbiter slaves" must "each have encoder IO for the number of masters passed to the constructor" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new CrossbarArbiter(numberOfMasters, anyNumberOfSlaves())
			forAll(arbiter.io.slaves) { slave => slave.encoder.inputs.length must be(numberOfMasters) }
		}}
	}

	they must "each have arbitration IO for the number of masters passed to the constructor" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) => {
			val arbiter = new CrossbarArbiter(numberOfMasters, anyNumberOfSlaves())
			forAll(arbiter.io.slaves) { slave => slave.masters.length must be(numberOfMasters) }
		}}
	}

	they must "have the same number of bits in the grantedMasterIndex as the encoder output" in spinalContext { () =>
		val arbiter = new CrossbarArbiter(anyNumberOfMasters(), anyNumberOfSlaves())
		forAll(arbiter.io.slaves) { slave => slave.grantedMasterIndex.getWidth must be(slave.encoder.output.getWidth) }
	}
}
