package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusMasterSlaveMapSimulationTest(numberOfMasters: Int, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusMasterSlaveMapFixture]
	with Inspectors {

	protected override def dutFactory() = new WishboneBusMasterSlaveMapFixture(numberOfMasters, numberOfSlaves, dutCreatedViaApplyFactory)

	"WishboneBusMasterSlaveMap masters" must "be able to index each slave" in simulator { fixture =>
		forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
			forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
				master.ADR #= fixture.addressFor(masterIndex, slaveIndex)
				sleep(1)
				fixture.io.dut.masters(masterIndex).index.toInt must be(slaveIndex)
			}
		}
	}

	they must "be able to flag a selection as valid for each slave" in simulator { fixture =>
		forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
			forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
				master.ADR #= fixture.addressFor(masterIndex, slaveIndex)
				sleep(1)
				fixture.io.dut.masters(masterIndex).isValid.toBoolean must be(true)
			}
		}
	}

	they must "have a zero index if the slave is out of bounds" in simulator { fixture =>
		forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
			forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
				master.ADR #= fixture.addressFor(masterIndex, fixture.outOfBoundsSlaveIndex)
				sleep(1)
				fixture.io.dut.masters(masterIndex).index.toInt must be(0)
			}
		}
	}

	they must "be able to flag a selection as invalid for a slave that is out of bounds" in simulator { fixture =>
		forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
			forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
				master.ADR #= fixture.addressFor(masterIndex, fixture.outOfBoundsSlaveIndex)
				sleep(1)
				fixture.io.dut.masters(masterIndex).isValid.toBoolean must be(false)
			}
		}
	}

	they must "have a zero index if a master selects multiple slaves" in simulator { fixture =>
		if (numberOfSlaves > 1) {
			forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
				forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
					master.ADR #= fixture.addressFor(masterIndex, fixture.multipleSlavesSelectedIndex)
					sleep(1)
					fixture.io.dut.masters(masterIndex).index.toInt must be(0)
				}
			}
		}
	}

	they must "be able to flag a selection as invalid when a master selects multiple slaves" in simulator { fixture =>
		if (numberOfSlaves > 1) {
			forAll(fixture.io.wishbone.masters.zipWithIndex) { case (master, masterIndex) =>
				forAll(fixture.io.wishbone.slaves.zipWithIndex) { case (slave, slaveIndex) =>
					master.ADR #= fixture.addressFor(masterIndex, fixture.multipleSlavesSelectedIndex)
					sleep(1)
					fixture.io.dut.masters(masterIndex).isValid.toBoolean must be(false)
				}
			}
		}
	}
}
