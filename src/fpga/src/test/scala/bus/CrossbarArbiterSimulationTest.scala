package net.restall.ice40riscvsoc.tests.bus

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MultiMasterSingleSlaveArbiter}
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class CrossbarArbiterSimulationTest(numberOfMasters: Int, numberOfSlaves: Int) extends AnyFlatSpec
	with LightweightSimulationFixture[CrossbarArbiterFixtureTraits]
	with Inspectors {

	protected override def dutFactory(): CrossbarArbiterFixtureTraits = new CrossbarArbiterFixture(numberOfMasters, numberOfSlaves)

	// TODO: Most of this is the same set of tests as MultiMasterSingleSlaveArbiterSimulationTest but there was no obvious way to share the tests.  Can it be done nicely ?

	"CrossbarArbiter slaves after reset" must "have the index of the granted master zeroed" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => true)
			fixture.reset()
			slave.grantedMasterIndex.toInt must be(0)
		}
	}

	private def setAllMasterRequestsTo(value: Int => Boolean)(implicit fixture: CrossbarArbiterFixtureTraits, slave: MultiMasterSingleSlaveArbiter.IoBundle) = {
		(0 until slave.masters.length).foreach(index => setMasterRequestTo(index, value(index)))
	}

	private def setMasterRequestTo(index: Int, value: Boolean)(implicit fixture: CrossbarArbiterFixtureTraits, slave: MultiMasterSingleSlaveArbiter.IoBundle) = {
		fixture.setMasterRequest(slave, index, value)
	}

	they must "have no master granted access to the bus when no master is requesting control, unless there is only one master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters) { master => master.isGranted.toBoolean must be(numberOfMasters == 1) }
		}
	}

	they must "have no master stalled waiting for access to the bus when no master is requesting control" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters) { master => master.isStalled.toBoolean must be(false) }
		}
	}

	they must "have all masters except the first stalled waiting for access to the bus when all masters are requesting control" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => true)
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, index) => master.isStalled.toBoolean must be(index != 0) }
		}
	}

	they must "have no master with an error flag" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => true)
			fixture.reset()
			forAll(slave.masters) { master => master.isError.toBoolean must be(false) }
		}
	}

	private def nextIntUpToExcept(maxExclusive: Int, except: Int): Int = {
		val value = Random.nextInt(maxExclusive)
		if (value != except) value else if (maxExclusive <= 1) except ^ 1 else nextIntUpToExcept(maxExclusive, except)
	}

	if (numberOfMasters > 1) {
		"CrossbarArbiter with multiple masters" must "latch the index of the single master requesting control when the encoder output is valid" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				val indexOfMasterRequestingControl = nextIntUpToExcept(slave.masters.length, slave.grantedMasterIndex.toInt)
				slave.encoder.output #= indexOfMasterRequestingControl
				slave.encoder.isValid #= true
				setAllMasterRequestsTo(masterIndex => masterIndex == indexOfMasterRequestingControl)
				fixture.reset()
				fixture.clockActive()
				sleep(1)
				slave.grantedMasterIndex.toInt must be(indexOfMasterRequestingControl)
			}
		}

		it must "not latch the index of the next master requesting control until relinquished" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				val indexOfFirstMasterRequestingControl = nextIntUpToExcept(slave.masters.length, slave.grantedMasterIndex.toInt)
				val indexOfSecondMasterRequestingControl = nextIntUpToExcept(slave.masters.length, indexOfFirstMasterRequestingControl)
				slave.encoder.output #= indexOfFirstMasterRequestingControl
				slave.encoder.isValid #= true
				setAllMasterRequestsTo(masterIndex => masterIndex == indexOfFirstMasterRequestingControl)

				fixture.reset()
				fixture.clock()
				setMasterRequestTo(indexOfSecondMasterRequestingControl, true)
				slave.encoder.output #= indexOfSecondMasterRequestingControl
				fixture.clockActive()
				sleep(1)
				slave.grantedMasterIndex.toInt must be(indexOfFirstMasterRequestingControl)
			}
		}

		it must "latch the index of the next master requesting control when the encoder output is valid and control has been relinquished" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				val indexOfFirstMasterRequestingControl = nextIntUpToExcept(slave.masters.length, slave.grantedMasterIndex.toInt)
				val indexOfSecondMasterRequestingControl = nextIntUpToExcept(slave.masters.length, indexOfFirstMasterRequestingControl)
				slave.encoder.output #= indexOfFirstMasterRequestingControl
				slave.encoder.isValid #= true
				setAllMasterRequestsTo(masterIndex => masterIndex == indexOfFirstMasterRequestingControl)

				fixture.reset()
				fixture.clock()
				setMasterRequestTo(indexOfFirstMasterRequestingControl, false)
				setMasterRequestTo(indexOfSecondMasterRequestingControl, true)
				slave.encoder.output #= indexOfSecondMasterRequestingControl
				fixture.clockActive()
				sleep(1)
				slave.grantedMasterIndex.toInt must be(indexOfSecondMasterRequestingControl)
			}
		}
	} else {
		"CrossbarArbiter with single master" must "hard-code the granted master when the encoder output is valid and the master is not requesting control" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 0
				slave.encoder.isValid #= true
				setMasterRequestTo(0, false)
				fixture.reset()
				fixture.clock()
				slave.grantedMasterIndex.toInt must be(0)
			}
		}

		it must "hard-code the granted master when the encoder output is valid and the master is requesting control" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 0
				slave.encoder.isValid #= true
				setMasterRequestTo(0, true)
				fixture.reset()
				fixture.clock()
				slave.grantedMasterIndex.toInt must be(0)
			}
		}

		it must "hard-code the granted master when the encoder output is not valid and the master is not requesting control" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 0
				slave.encoder.isValid #= false
				setMasterRequestTo(0, false)
				fixture.reset()
				fixture.clock()
				slave.grantedMasterIndex.toInt must be(0)
			}
		}

		it must "hard-code the granted master when the encoder output is not valid and the master is requesting control" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 0
				slave.encoder.isValid #= false
				setMasterRequestTo(0, true)
				fixture.reset()
				fixture.clock()
				slave.grantedMasterIndex.toInt must be(0)
			}
		}

		it must "hard-code the granted master when the encoder output is out of bounds" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 1
				slave.encoder.isValid #= true
				setMasterRequestTo(0, true)
				fixture.reset()
				fixture.clock()
				slave.grantedMasterIndex.toInt must be(0)
			}
		}

		it must "hard-code the stalled flag to false when requesting whilst the encoder output indicates another master has been granted" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 1
				slave.encoder.isValid #= true
				setMasterRequestTo(0, true)
				fixture.reset()
				fixture.clock()
				slave.masters(0).isStalled.toBoolean must be(false)
			}
		}

		it must "hard-code the granted flag to true when requesting whilst the encoder output indicates another master has been granted" in simulator { implicit fixture =>
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.output #= 1
				slave.encoder.isValid #= true
				setMasterRequestTo(0, true)
				fixture.reset()
				fixture.clock()
				slave.masters(0).isGranted.toBoolean must be(true)
			}
		}
	}

	"CrossbarArbiter encoder inputs" must "all be false when no masters are requesting to be granted bus access, unless there is only one master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			setAllMasterRequestsTo(_ => false)
			sleep(1)
			slave.encoder.inputs.map(_.toBoolean) must be(Seq.fill(numberOfMasters) { numberOfMasters == 1 })
		}
	}

	they must "all be true when all masters are requesting to be granted bus access" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			setAllMasterRequestsTo(_ => true)
			sleep(1)
			slave.encoder.inputs.map(_.toBoolean) must be(Seq.fill(numberOfMasters) { true })
		}
	}

	they must "be the asynchronous value of the master request lines, unless there is only one master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			val requests = Seq.fill(numberOfMasters) { if (numberOfMasters > 1) Random.nextBoolean() else false }
			setAllMasterRequestsTo(masterIndex => requests(masterIndex))
			sleep(1)
			slave.encoder.inputs.map(_.toBoolean) must be(if (numberOfMasters == 1) Seq(true) else requests)
		}
	}

	"CrossbarArbiter masters" must "not be granted control when they are not requesting it, unless there is only one master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				slave.encoder.output #= masterIndex
				fixture.clock()
				master.isGranted.toBoolean must be(numberOfMasters == 1)
			}
		}
	}

	they must "not have an error when they are not requesting control and the encoder output is valid" in simulator { implicit fixture =>
		notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(true)
	}

	private def notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(isValid: Boolean)(implicit fixture: CrossbarArbiterFixtureTraits) = {
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= isValid
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				slave.encoder.output #= masterIndex
				fixture.clock()
				master.isError.toBoolean must be(false)
			}
		}
	}

	they must "not have an error when they are not requesting control but the encoder output is not valid" in simulator { implicit fixture =>
		notHaveAnErrorWhenTheyAreNotRequestingControlAndTheEncoderOutputIs(false)
	}

	they must "have an error when they are requesting control and the encoder output is not valid, or be hard-coded false if there is only a single master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= false
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				setMasterRequestTo(masterIndex, true)
				slave.encoder.output #= masterIndex
				fixture.clock()
				master.isError.toBoolean must be(numberOfMasters != 1)
			}
		}
	}

	they must "not be stalled when they are not requesting control" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				slave.encoder.output #= masterIndex
				fixture.clock()
				master.isStalled.toBoolean must be(false)
			}
		}
	}

	they must "not be stalled when they are requesting control and are granted it" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				setAllMasterRequestsTo(_ => false)
				slave.encoder.output #= masterIndex
				setMasterRequestTo(masterIndex, true)
				fixture.clock()
				master.isStalled.toBoolean must be(false)
			}
		}
	}

	they must "be stalled when they are requesting control and another master is granted control" in simulator { implicit fixture =>
		if (numberOfMasters > 1) {
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.isValid #= true
				fixture.reset()
				forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
					setAllMasterRequestsTo(_ => false)
					val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
					setMasterRequestTo(anotherMaster, true)
					slave.encoder.output #= anotherMaster
					fixture.clock()
					slave.encoder.output #= masterIndex
					setMasterRequestTo(masterIndex, true)
					sleep(1)
					master.isStalled.toBoolean must be(true)
				}
			}
		}
	}

	they must "not be granted control, unless there is only one master, when they are not requesting control but the encoder indicates another master" in simulator { implicit fixture =>
		forAll(fixture.io.slaves) { implicit slave =>
			slave.encoder.isValid #= true
			setAllMasterRequestsTo(_ => false)
			fixture.reset()
			forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
				slave.encoder.output #= nextIntUpToExcept(numberOfMasters, masterIndex)
				fixture.clock()
				master.isGranted.toBoolean must be(numberOfMasters == 1)
			}
		}
	}

	they must "not be granted control when they are requesting control and another master is granted control" in simulator { implicit fixture =>
		if (numberOfMasters > 1) {
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.isValid #= true
				fixture.reset()
				forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
					setAllMasterRequestsTo(_ => false)
					val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
					setMasterRequestTo(anotherMaster, true)
					slave.encoder.output #= anotherMaster
					fixture.clock()
					setMasterRequestTo(masterIndex, true)
					slave.encoder.output #= masterIndex
					fixture.clock()
					master.isGranted.toBoolean must be(false)
				}
			}
		}
	}

	they must "be granted control when they are requesting control and the granted master index is out of bounds" in simulator { implicit fixture =>
		if (numberOfMasters == 1) {
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.isValid #= true
				fixture.reset()
				forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
					setAllMasterRequestsTo(_ => false)
					val outOfBoundsMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
					slave.encoder.output #= outOfBoundsMaster
					fixture.clock()
					setMasterRequestTo(masterIndex, true)
					fixture.clock()
					master.isGranted.toBoolean must be(true)
				}
			}
		}
	}

	they must "be granted when they are requesting control and the previous master has relinquished control" in simulator { implicit fixture =>
		if (numberOfMasters > 1) {
			forAll(fixture.io.slaves) { implicit slave =>
				slave.encoder.isValid #= true
				fixture.reset()
				forAll(slave.masters.zipWithIndex) { case (master, masterIndex) =>
					setAllMasterRequestsTo(_ => false)
					val anotherMaster = nextIntUpToExcept(numberOfMasters, masterIndex)
					setMasterRequestTo(anotherMaster, true)
					slave.encoder.output #= anotherMaster
					fixture.clock()
					setMasterRequestTo(anotherMaster, false)
					slave.encoder.output #= masterIndex
					setMasterRequestTo(masterIndex, true)
					fixture.clock()
					master.isGranted.toBoolean must be(true)
				}
			}
		}
	}
}
