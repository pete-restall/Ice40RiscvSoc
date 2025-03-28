package uk.co.lophtware.msfreference.tests.bus

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.bus.MultiMasterSingleSlaveArbiter
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class MultiMasterSingleSlaveArbiterSimulationTest(numberOfMasters: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[MultiMasterSingleSlaveArbiterFixture]
	with Inspectors {

	protected override def dutFactory() = new MultiMasterSingleSlaveArbiterFixture(numberOfMasters, dutCreatedViaApplyFactory)

	"MultiMasterSingleSlaveArbiter after reset" must "have the index of the granted master zeroed" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= true)
		fixture.reset()
		fixture.io.grantedMasterIndex.toInt must be(0)
	}

	it must "have no master granted access to the bus when no master is requesting control, unless there is only one master" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters) { master => master.isGranted.toBoolean must be(numberOfMasters == 1) }
	}

	it must "have no master stalled waiting for access to the bus when no master is requesting control" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters) { master => master.isStalled.toBoolean must be(false) }
	}

	it must "have all masters except the first stalled waiting for access to the bus when all masters are requesting control" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= true)
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, index) => master.isStalled.toBoolean must be(index != 0) }
	}

	it must "have no master with an error flag" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= true)
		fixture.reset()
		forAll(fixture.io.masters) { master => master.isError.toBoolean must be(false) }
	}

	private def nextIntUpToExcept(maxExclusive: Int, except: Int): Int = {
		val value = Random.nextInt(maxExclusive)
		if (value != except) value else if (maxExclusive <= 1) except ^ 1 else nextIntUpToExcept(maxExclusive, except)
	}

	if (numberOfMasters > 1) {
		"MultiMasterSingleSlaveArbiter with multiple masters" must "latch the index of the single master requesting control when the encoder output is valid" in simulator { fixture =>
			val indexOfMasterRequestingControl = nextIntUpToExcept(fixture.io.masters.length, fixture.io.grantedMasterIndex.toInt)
			fixture.io.encoder.output #= indexOfMasterRequestingControl
			fixture.io.encoder.isValid #= true
			fixture.io.masters.zipWithIndex.foreach { case (master, masterIndex) => master.request #= masterIndex == indexOfMasterRequestingControl }
			fixture.reset()
			fixture.clockActive()
			sleep(1)
			fixture.io.grantedMasterIndex.toInt must be(indexOfMasterRequestingControl)
		}

		it must "not latch the index of the next master requesting control until relinquished" in simulator { fixture =>
			val indexOfFirstMasterRequestingControl = nextIntUpToExcept(fixture.io.masters.length, fixture.io.grantedMasterIndex.toInt)
			val indexOfSecondMasterRequestingControl = nextIntUpToExcept(fixture.io.masters.length, indexOfFirstMasterRequestingControl)
			fixture.io.encoder.output #= indexOfFirstMasterRequestingControl
			fixture.io.encoder.isValid #= true
			fixture.io.masters.zipWithIndex.foreach { case (master, masterIndex) => master.request #= masterIndex == indexOfFirstMasterRequestingControl }

			fixture.reset()
			fixture.clock()
			fixture.io.masters(indexOfSecondMasterRequestingControl).request #= true
			fixture.io.encoder.output #= indexOfSecondMasterRequestingControl
			fixture.clockActive()
			sleep(1)
			fixture.io.grantedMasterIndex.toInt must be(indexOfFirstMasterRequestingControl)
		}

		it must "latch the index of the next master requesting control when the encoder output is valid and control has been relinquished" in simulator { fixture =>
			val indexOfFirstMasterRequestingControl = nextIntUpToExcept(fixture.io.masters.length, fixture.io.grantedMasterIndex.toInt)
			val indexOfSecondMasterRequestingControl = nextIntUpToExcept(fixture.io.masters.length, indexOfFirstMasterRequestingControl)
			fixture.io.encoder.output #= indexOfFirstMasterRequestingControl
			fixture.io.encoder.isValid #= true
			fixture.io.masters.zipWithIndex.foreach { case (master, masterIndex) => master.request #= masterIndex == indexOfFirstMasterRequestingControl }

			fixture.reset()
			fixture.clock()
			fixture.io.masters(indexOfFirstMasterRequestingControl).request #= false
			fixture.io.masters(indexOfSecondMasterRequestingControl).request #= true
			fixture.io.encoder.output #= indexOfSecondMasterRequestingControl
			fixture.clockActive()
			sleep(1)
			fixture.io.grantedMasterIndex.toInt must be(indexOfSecondMasterRequestingControl)
		}
	} else {
		"MultiMasterSingleSlaveArbiter with single master" must "hard-code the granted master when the encoder output is valid and the master is not requesting control" in simulator { fixture =>
			fixture.io.encoder.output #= 0
			fixture.io.encoder.isValid #= true
			fixture.io.masters(0).request #= false
			fixture.reset()
			fixture.clock()
			fixture.io.grantedMasterIndex.toInt must be(0)
		}

		it must "hard-code the granted master when the encoder output is valid and the master is requesting control" in simulator { fixture =>
			fixture.io.encoder.output #= 0
			fixture.io.encoder.isValid #= true
			fixture.io.masters(0).request #= true
			fixture.reset()
			fixture.clock()
			fixture.io.grantedMasterIndex.toInt must be(0)
		}

		it must "hard-code the granted master when the encoder output is not valid and the master is not requesting control" in simulator { fixture =>
			fixture.io.encoder.output #= 0
			fixture.io.encoder.isValid #= false
			fixture.io.masters(0).request #= false
			fixture.reset()
			fixture.clock()
			fixture.io.grantedMasterIndex.toInt must be(0)
		}

		it must "hard-code the granted master when the encoder output is not valid and the master is requesting control" in simulator { fixture =>
			fixture.io.encoder.output #= 0
			fixture.io.encoder.isValid #= false
			fixture.io.masters(0).request #= true
			fixture.reset()
			fixture.clock()
			fixture.io.grantedMasterIndex.toInt must be(0)
		}

		it must "hard-code the granted master when the encoder output is out of bounds" in simulator { fixture =>
			fixture.io.encoder.output #= 1
			fixture.io.encoder.isValid #= true
			fixture.io.masters(0).request #= true
			fixture.reset()
			fixture.clock()
			fixture.io.grantedMasterIndex.toInt must be(0)
		}

		it must "hard-code the stalled flag to false when requesting whilst the encoder output indicates another master has been granted" in simulator { fixture =>
			fixture.io.encoder.output #= 1
			fixture.io.encoder.isValid #= true
			fixture.io.masters(0).request #= true
			fixture.reset()
			fixture.clock()
			fixture.io.masters(0).isStalled.toBoolean must be(false)
		}

		it must "hard-code the granted flag to true when requesting whilst the encoder output indicates another master has been granted" in simulator { fixture =>
			fixture.io.encoder.output #= 1
			fixture.io.encoder.isValid #= true
			fixture.io.masters(0).request #= true
			fixture.reset()
			fixture.clock()
			fixture.io.masters(0).isGranted.toBoolean must be(true)
		}
	}

	"MultiMasterSingleSlaveArbiter encoder inputs" must "all be false when no masters are requesting to be granted bus access, unless there is only one master" in simulator { fixture =>
		fixture.io.masters.foreach(_.request #= false)
		sleep(1)
		fixture.io.encoder.inputs.map(_.toBoolean) must be(Seq.fill(numberOfMasters) { numberOfMasters == 1 })
	}

	they must "all be true when all masters are requesting to be granted bus access" in simulator { fixture =>
		fixture.io.masters.foreach(_.request #= true)
		sleep(1)
		fixture.io.encoder.inputs.map(_.toBoolean) must be(Seq.fill(numberOfMasters) { true })
	}

	they must "be the asynchronous value of the master request lines, unless there is only one master" in simulator { fixture =>
		val requests = Seq.fill(numberOfMasters) { if (numberOfMasters > 1) Random.nextBoolean() else false }
		fixture.io.masters.zip(requests).foreach { case (master, request) => master.request #= request }
		sleep(1)
		fixture.io.encoder.inputs.map(_.toBoolean) must be(if (numberOfMasters == 1) Seq(true) else requests)
	}

	"MultiMasterSingleSlaveArbiter masters" must "not be granted control when they are not requesting it, unless there is only one master" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			fixture.io.encoder.output #= masterIndex
			fixture.clock()
			master.isGranted.toBoolean must be(numberOfMasters == 1)
		}
	}

	they must "not have an error when they are not requesting control and the encoder output is valid" in simulator { implicit fixture =>
		notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(true)
	}

	private def notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(isValid: Boolean)(implicit fixture: MultiMasterSingleSlaveArbiterFixture) = {
		fixture.io.encoder.isValid #= isValid
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			fixture.io.encoder.output #= masterIndex
			fixture.clock()
			master.isError.toBoolean must be(false)
		}
	}

	they must "not have an error when they are not requesting control but the encoder output is not valid" in simulator { implicit fixture =>
		notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(false)
	}

	they must "have an error when they are requesting control and the encoder output is not valid, or be hard-coded false if there is only a single master" in simulator { fixture =>
		fixture.io.encoder.isValid #= false
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			master.request #= true
			fixture.io.encoder.output #= masterIndex
			fixture.clock()
			master.isError.toBoolean must be(numberOfMasters != 1)
		}
	}

	they must "not be stalled when they are not requesting control" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			fixture.io.encoder.output #= masterIndex
			fixture.clock()
			master.isStalled.toBoolean must be(false)
		}
	}

	they must "not be stalled when they are requesting control and are granted it" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			fixture.io.masters.foreach(_.request #= false)
			fixture.io.encoder.output #= masterIndex
			master.request #= true
			fixture.clock()
			master.isStalled.toBoolean must be(false)
		}
	}

	they must "be stalled when they are requesting control and another master is granted control" in simulator { fixture =>
		if (numberOfMasters > 1) {
			fixture.io.encoder.isValid #= true
			fixture.reset()
			forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
				fixture.io.masters.foreach(_.request #= false)
				val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
				fixture.io.masters(anotherMaster).request #= true
				fixture.io.encoder.output #= anotherMaster
				fixture.clock()
				fixture.io.encoder.output #= masterIndex
				master.request #= true
				sleep(1)
				master.isStalled.toBoolean must be(true)
			}
		}
	}

	they must "not be granted control, unless there is only one master, when they are not requesting control but the encoder indicates another master" in simulator { fixture =>
		fixture.io.encoder.isValid #= true
		fixture.io.masters.foreach(_.request #= false)
		fixture.reset()
		forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
			fixture.io.encoder.output #= nextIntUpToExcept(numberOfMasters, masterIndex)
			fixture.clock()
			master.isGranted.toBoolean must be(numberOfMasters == 1)
		}
	}

	they must "not be granted control when they are requesting control and another master is granted control" in simulator { fixture =>
		if (numberOfMasters > 1) {
			fixture.io.encoder.isValid #= true
			fixture.reset()
			forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
				fixture.io.masters.foreach(_.request #= false)
				val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
				fixture.io.masters(anotherMaster).request #= true
				fixture.io.encoder.output #= anotherMaster
				fixture.clock()
				master.request #= true
				fixture.io.encoder.output #= masterIndex
				fixture.clock()
				master.isGranted.toBoolean must be(false)
			}
		}
	}

	they must "be granted control when they are requesting control and the granted master index is out of bounds" in simulator { fixture =>
		if (numberOfMasters == 1) {
			fixture.io.encoder.isValid #= true
			fixture.reset()
			forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
				fixture.io.masters.foreach(_.request #= false)
				val outOfBoundsMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
				fixture.io.encoder.output #= outOfBoundsMaster
				fixture.clock()
				master.request #= true
				fixture.clock()
				master.isGranted.toBoolean must be(true)
			}
		}
	}

	they must "be granted when they are requesting control and the previous master has relinquished control" in simulator { fixture =>
		if (numberOfMasters > 1) {
			fixture.io.encoder.isValid #= true
			fixture.reset()
			forAll(fixture.io.masters.zipWithIndex) { case (master, masterIndex) =>
				fixture.io.masters.foreach(_.request #= false)
				val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
				fixture.io.masters(anotherMaster).request #= true
				fixture.io.encoder.output #= anotherMaster
				fixture.clock()
				fixture.io.masters(anotherMaster).request #= false
				fixture.io.encoder.output #= masterIndex
				master.request #= true
				fixture.clock()
				master.isGranted.toBoolean must be(true)
			}
		}
	}
}
