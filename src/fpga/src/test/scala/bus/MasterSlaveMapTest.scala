package uk.co.lophtware.msfreference.tests.bus

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.bus.MasterSlaveMap
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class MasterSlaveMapTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"MasterSlaveMap" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val slaveMap = new MasterSlaveMap[FakeBus](dummyMasters(), dummySlaves())
		slaveMap.io.name must be("")
	}

	private def dummyMasters() = Seq.fill(anyNumberOfMasters()) { dummyMaster() }

	private def anyNumberOfMasters() = Random.between(1, 64)

	private def dummyMaster() = FakeBusTestDoubles.stubMaster()

	private def dummySlaves() = Seq.fill(anyNumberOfSlaves()) { dummySlave() }

	private def anyNumberOfSlaves() = Random.between(1, 64)

	private def dummySlave() = FakeBusTestDoubles.stubSlave()

	it must "not accept null masters" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new MasterSlaveMap[FakeBus](null, dummySlaves()))
		thrown.getMessage must (include("arg=masters") and include("null"))
	}

	it must "not accept any null masters" in spinalContext { () =>
		val mastersContainingNull = Random.shuffle(dummyMasters() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy new MasterSlaveMap[FakeBus](mastersContainingNull, dummySlaves())
		thrown.getMessage must (include("arg=masters") and include("null"))
	}

	it must "not accept less than one master" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new MasterSlaveMap[FakeBus](Seq(), dummyMasters()))
		thrown.getMessage must (include("arg=masters") and include("empty"))
	}

	it must "not accept any duplicate masters" in spinalContext { () =>
		val duplicate = dummyMaster()
		val mastersContainingDuplicates = Random.shuffle(dummyMasters() :+ duplicate :+ duplicate)
		val thrown = the [IllegalArgumentException] thrownBy new MasterSlaveMap[FakeBus](mastersContainingDuplicates, dummySlaves())
		thrown.getMessage must (include("arg=masters") and include("duplicate"))
	}

	it must "have a masters property that is the same value as passed to the constructor" in spinalContext { () =>
		val masters = dummyMasters()
		val slaveMap = new MasterSlaveMap[FakeBus](masters, dummySlaves())
		slaveMap.masters must be(masters)
	}

	it must "not accept null slaves" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new MasterSlaveMap[FakeBus](dummyMasters(), null))
		thrown.getMessage must (include("arg=slaves") and include("null"))
	}

	it must "not accept any null slaves" in spinalContext { () =>
		val slavesContainingNull = Random.shuffle(dummySlaves() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy new MasterSlaveMap[FakeBus](dummyMasters(), slavesContainingNull)
		thrown.getMessage must (include("arg=slaves") and include("null"))
	}

	it must "not accept less than one slave" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new MasterSlaveMap[FakeBus](dummyMasters(), Seq()))
		thrown.getMessage must (include("arg=slaves") and include("empty"))
	}

	it must "not accept any duplicate slaves" in spinalContext { () =>
		val duplicate = dummySlave()
		val slavesContainingDuplicates = Random.shuffle(dummySlaves() :+ duplicate :+ duplicate)
		val thrown = the [IllegalArgumentException] thrownBy new MasterSlaveMap[FakeBus](dummyMasters(), slavesContainingDuplicates)
		thrown.getMessage must (include("arg=slaves") and include("duplicate"))
	}

	it must "have a slaves property that is the same value as passed to the constructor" in spinalContext { () =>
		val slaves = dummySlaves()
		val slaveMap = new MasterSlaveMap[FakeBus](dummyMasters(), slaves)
		slaveMap.slaves must be(slaves)
	}

	private val numberOfMasters = Seq(1, 2, 3, 4 + anyNumberOfMasters()).asTable("numberOfMasters")

	it must "have the same number of IO as masters" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val slaveMap = new MasterSlaveMap[FakeBus](Seq.fill(numberOfMasters) { dummyMaster() }, dummySlaves())
			slaveMap.io.masters.length must be(numberOfMasters)
		}
	}

	private val numberOfSlaves = Seq(1, 2, 3, 4 + anyNumberOfSlaves()).asTable("numberOfSlaves")

	it must "have the same number of IO as slaves" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) =>
			val slaveMap = new MasterSlaveMap[FakeBus](dummyMasters(), Seq.fill(numberOfSlaves) { dummySlave() })
			forAll(slaveMap.io.masters) { master => master.slaveSelects.length must be(numberOfSlaves) }
		}
	}

	private val slavesVsIndexWidth = Seq(
		(1, 1 bit),
		(2, 1 bit),
		(3, 2 bits),
		(4, 2 bits),
		(5, 3 bits),
		(6, 3 bits),
		(7, 3 bits),
		(8, 3 bits),
		(9, 4 bits),
		(16, 4 bits),
		(17, 5 bits),
		(128, 7 bits)
	).asTable("numberOfSlaves", "indexWidth")

	it must "have the slave selector IO wide enough for the given number of slaves" in spinalContext { () =>
		forAll(slavesVsIndexWidth) { (numberOfSlaves: Int, indexWidth: BitCount) =>
			val slaveMap = new MasterSlaveMap[FakeBus](dummyMasters(), Seq.fill(numberOfSlaves) { dummySlave() })
			forAll(slaveMap.io.masters) { master => master.index.getWidth must be(indexWidth.value) }
		}
	}

	"MasterSlaveMap companion's apply() method" must "not accept a null first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(null)
		thrown.getMessage must (include("arg=firstMapping") and include("null"))
	}

	it must "not accept any null mappings" in spinalContext { () =>
		val otherMappingsWithNull = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(dummyMapping(), otherMappingsWithNull:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null"))
	}

	private def dummyMapping() = stubMappingWithMaster(dummyMaster())

	private def stubMappingWithMaster(master: FakeBus) = (master, (bus: FakeBus) => False, dummySlave())

	it must "not accept a null master in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(stubMappingWithMaster(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("master"))
	}

	it must "not accept a null selector in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(stubMappingWithSelector(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	private def stubMappingWithSelector(selector: FakeBus => Bool) = (dummyMaster(), selector, dummySlave())

	it must "not accept a null selector result in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(stubMappingWithSelector(_ => null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	it must "not accept a null slave in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(stubMappingWithSlave(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("slave"))
	}

	private def stubMappingWithSlave(slave: FakeBus) = (dummyMaster(), (bus: FakeBus) => False, slave)

	it must "not accept a null master in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullMaster = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithMaster(null))
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(dummyMapping(), otherMappingsContainingNullMaster:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("master"))
	}

	it must "not accept a null selector in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSelector = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(null))
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelector:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null selector result in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSelectorResult = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(_ => null))
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelectorResult:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null slave in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSlave = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSlave(null))
		val thrown = the [IllegalArgumentException] thrownBy MasterSlaveMap(dummyMapping(), otherMappingsContainingNullSlave:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("slave"))
	}

	it must "hard-code any unmapped master-slave selection to false" in spinalContext { () =>
		val masters = Seq(dummyMaster(), dummyMaster())
		val slaves = Seq(dummySlave(), dummySlave())
		val map = MasterSlaveMap(
			(masters(0), (bus: FakeBus) => bus.address =/= 0, slaves(0)),
			(masters(1), (bus: FakeBus) => bus.address =/= 0, slaves(1)))

		map.io.masters(0).slaveSelects(1).hasAssignement must be(true)
	}
}
