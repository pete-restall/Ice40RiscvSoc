package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusMasterSlaveMapTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusMasterSlaveMap" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val slaveMap = new WishboneBusMasterSlaveMap(dummyMasters(), dummySlaves())
		slaveMap.io.name must be("")
	}

	private val dummyConfig = WishboneConfigTestDoubles.dummy()

	private def dummyMasters() = Seq.fill(anyNumberOfMasters()) { dummyMaster() }

	private def anyNumberOfMasters() = Random.between(1, 64)

	private def dummyMaster() = WishboneTestDoubles.stubMasterWith(dummyConfig)

	private def dummySlaves() = Seq.fill(anyNumberOfSlaves()) { dummySlave() }

	private def anyNumberOfSlaves() = Random.between(1, 64)

	private def dummySlave() = WishboneTestDoubles.stubSlaveWith(dummyConfig)

	it must "not accept null masters" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterSlaveMap(null, dummySlaves()))
		thrown.getMessage must (include("arg=masters") and include("null"))
	}

	it must "not accept any null masters" in spinalContext { () =>
		val mastersContainingNull = Random.shuffle(dummyMasters() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(mastersContainingNull, dummySlaves())
		thrown.getMessage must (include("arg=masters") and include("null"))
	}

	it must "not accept less than one master" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterSlaveMap(Seq(), dummyMasters()))
		thrown.getMessage must (include("arg=masters") and include("empty"))
	}

	it must "not accept any duplicate masters" in spinalContext { () =>
		val duplicate = dummyMaster()
		val mastersContainingDuplicates = Random.shuffle(dummyMasters() :+ duplicate :+ duplicate)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(mastersContainingDuplicates, dummySlaves())
		thrown.getMessage must (include("arg=masters") and include("duplicate"))
	}

	it must "not accept any masters that have different bus configurations" in spinalContext { () =>
		val differingMaster = WishboneTestDoubles.stubMasterWith(WishboneConfigTestDoubles.stubDifferentTo(dummyConfig))
		val mastersContainingDifferences = Random.shuffle(dummyMasters() :+ differingMaster)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(mastersContainingDifferences, dummySlaves())
		thrown.getMessage must (include("arg=masters") and include("identical"))
	}

	it must "have a masters property that is the same value as passed to the constructor" in spinalContext { () =>
		val masters = dummyMasters()
		val slaveMap = new WishboneBusMasterSlaveMap(masters, dummySlaves())
		slaveMap.masters must be(masters)
	}

	it must "not accept null slaves" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterSlaveMap(dummyMasters(), null))
		thrown.getMessage must (include("arg=slaves") and include("null"))
	}

	it must "not accept any null slaves" in spinalContext { () =>
		val slavesContainingNull = Random.shuffle(dummySlaves() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(dummyMasters(), slavesContainingNull)
		thrown.getMessage must (include("arg=slaves") and include("null"))
	}

	it must "not accept less than one slave" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterSlaveMap(dummyMasters(), Seq()))
		thrown.getMessage must (include("arg=slaves") and include("empty"))
	}

	it must "not accept any duplicate slaves" in spinalContext { () =>
		val duplicate = dummySlave()
		val slavesContainingDuplicates = Random.shuffle(dummySlaves() :+ duplicate :+ duplicate)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(dummyMasters(), slavesContainingDuplicates)
		thrown.getMessage must (include("arg=slaves") and include("duplicate"))
	}

	it must "not accept any slaves that have different bus configurations" in spinalContext { () =>
		val differingSlave = WishboneTestDoubles.stubSlaveWith(WishboneConfigTestDoubles.stubDifferentTo(dummyConfig))
		val slavesContainingDifferences = Random.shuffle(dummySlaves() :+ differingSlave)
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(dummyMasters(), slavesContainingDifferences)
		thrown.getMessage must (include("arg=slaves") and include("identical"))
	}

	it must "not accept any difference in bus configurations between masters and slaves" in spinalContext { () =>
		val differentConfig = WishboneConfigTestDoubles.stubDifferentTo(dummyConfig)
		val slavesWithNonMasterConfig = Seq.fill(Random.between(1, 10)) { WishboneTestDoubles.stubSlaveWith(differentConfig) }
		val thrown = the [IllegalArgumentException] thrownBy new WishboneBusMasterSlaveMap(dummyMasters(), slavesWithNonMasterConfig)
		thrown.getMessage must (include("arg=slaves") and include("identical"))
	}

	it must "have a slaves property that is the same value as passed to the constructor" in spinalContext { () =>
		val slaves = dummySlaves()
		val slaveMap = new WishboneBusMasterSlaveMap(dummyMasters(), slaves)
		slaveMap.slaves must be(slaves)
	}

	private val numberOfMasters = tableFor("numberOfMasters", List(1, 2, 3, 4 + anyNumberOfMasters()))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "have the same number of IO as masters" in spinalContext { () =>
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val slaveMap = new WishboneBusMasterSlaveMap(Seq.fill(numberOfMasters) { dummyMaster() }, dummySlaves())
			slaveMap.io.masters.length must be(numberOfMasters)
		}
	}

	private val numberOfSlaves = tableFor("numberOfSlaves", List(1, 2, 3, 4 + anyNumberOfSlaves()))

	it must "have the same number of IO as slaves" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) =>
			val slaveMap = new WishboneBusMasterSlaveMap(dummyMasters(), Seq.fill(numberOfSlaves) { dummySlave() })
			forAll(slaveMap.io.masters) { master => master.slaveSelects.length must be(numberOfSlaves) }
		}
	}

	private val slavesVsIndexWidth = tableFor(("numberOfSlaves", "indexWidth"), List(
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
		(128, 7 bits)))

	private def tableFor[A, B](headers: (String, String), values: Iterable[(A, B)]) = Table(headers) ++ values

	it must "have the slave selector IO wide enough for the given number of slaves" in spinalContext { () =>
		forAll(slavesVsIndexWidth) { (numberOfSlaves: Int, indexWidth: BitCount) =>
			val slaveMap = new WishboneBusMasterSlaveMap(dummyMasters(), Seq.fill(numberOfSlaves) { dummySlave() })
			forAll(slaveMap.io.masters) { master => master.index.getWidth must be(indexWidth.value) }
		}
	}

	"WishboneBusMasterSlaveMap companion's apply() method" must "not accept a null first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(null)
		thrown.getMessage must (include("arg=firstMapping") and include("null"))
	}

	it must "not accept any null mappings" in spinalContext { () =>
		val otherMappingsWithNull = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsWithNull:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null"))
	}

	private def dummyMapping() = stubMappingWithMaster(dummyMaster())

	private def stubMappingWithMaster(master: Wishbone) = (master, (m: Wishbone) => False, dummySlave())

	it must "not accept a null master in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithMaster(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("master"))
	}

	it must "not accept a null selector in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSelector(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	private def stubMappingWithSelector(selector: Wishbone => Bool) = (dummyMaster(), selector, dummySlave())

	it must "not accept a null selector result in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSelector(_ => null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	it must "not accept a null slave in the first mapping" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSlave(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("slave"))
	}

	private def stubMappingWithSlave(slave: Wishbone) = (dummyMaster(), (m: Wishbone) => False, slave)

	it must "not accept a null master in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullMaster = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithMaster(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullMaster:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("master"))
	}

	it must "not accept a null selector in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSelector = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelector:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null selector result in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSelectorResult = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(_ => null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelectorResult:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null slave in any of the mappings" in spinalContext { () =>
		val otherMappingsContainingNullSlave = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSlave(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSlave:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("slave"))
	}

	it must "hard-code any unmapped master-slave selection to false" in spinalContext { () =>
		val masters = Seq(dummyMaster(), dummyMaster())
		val slaves = Seq(dummySlave(), dummySlave())
		val map = WishboneBusMasterSlaveMap(
			(masters(0), m => m.ADR =/= 0, slaves(0)),
			(masters(1), m => m.ADR =/= 0, slaves(1)))

		map.io.masters(0).slaveSelects(1).hasAssignement must be(true)
	}
}
