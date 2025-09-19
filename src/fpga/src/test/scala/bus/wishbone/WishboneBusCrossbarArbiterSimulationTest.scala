package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.{Inspectors, Suite}
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MasterSlaveMap, MultiMasterSingleSlaveArbiter}
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.bus.CrossbarArbiterSimulationTest
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusCrossbarArbiterSimulationTest(numberOfMasters: Int, numberOfSlaves: Int, busMap: => MasterSlaveMap[Wishbone]) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusCrossbarArbiterFixture]
	with Inspectors {

	protected override def dutFactory() = new WishboneBusCrossbarArbiterFixture(busMap)

	override def nestedSuites: IndexedSeq[Suite] = Array(new CrossbarArbiterSimulationTest(numberOfMasters, numberOfSlaves) {
		protected override def dutFactory() = new WishboneBusCrossbarArbiterFixture(busMap)
	})

	"WishboneBusCrossbarArbiter masters" must "not request control of the bus if the slave encoder is not valid" in simulator { fixture =>
		if (numberOfSlaves > 1) {
			fixture.reset()
			fixture.io.wishbone.masters.foreach { master =>
				master.CYC #= true
				master.ADR #= WishboneBusCrossbarArbiterFixture.AllSlavesSelected
			}
			sleep(1)
			forAll(fixture.io.slaves.flatMap(_.masters)) { master => master.request.toBoolean must be(false) }
		}
	}

	they must "not request control of the bus for any slaves other than indicated by the encoder" in simulator { implicit fixture =>
		if (numberOfSlaves > 1) {
			fixture.reset()
			forAll(fixture.io.slaves) { slave =>
				slave.masters.zipWithIndex.foreach { case (master, masterIndex) =>
					fixture.setMasterRequest(anySlaveExcept(slave), masterIndex, true)
					sleep(1)
					master.request.toBoolean must be(false)
				}
			}
		}
	}

	private def anySlaveExcept(slave: MultiMasterSingleSlaveArbiter.IoBundle)(implicit fixture: WishboneBusCrossbarArbiterFixture): MultiMasterSingleSlaveArbiter.IoBundle = {
		val slaveIndex = fixture.io.slaves.indexOf(slave)
		val differentSlaveIndex = Random.nextInt(fixture.io.slaves.length)
		if (differentSlaveIndex != slaveIndex) fixture.io.slaves(differentSlaveIndex) else anySlaveExcept(slave)
	}

	they must "not request control of the bus when CYC is low" in simulator { fixture =>
		fixture.reset()
		forAll(fixture.io.slaves) { slave =>
			slave.masters.zipWithIndex.foreach { case (master, masterIndex) =>
				fixture.setMasterRequest(slave, masterIndex, false)
				sleep(1)
				master.request.toBoolean must be(false)
			}
		}
	}

	they must "only request control of the bus when CYC is high and the encoder output is valid for the given slave" in simulator { fixture =>
		fixture.reset()
		forAll(fixture.io.slaves) { slave =>
			slave.masters.zipWithIndex.foreach { case (master, masterIndex) =>
				fixture.setMasterRequest(slave, masterIndex, true)
				sleep(1)
				master.request.toBoolean must be(true)
			}
		}
	}
}
