package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusMasterSlaveMapTest extends AnyFlatSpec with NonSimulationFixture {
	private val dummyConfig = WishboneConfigTestDoubles.dummy()

	"WishboneBusMasterSlaveMap companion's apply() method" must "not accept a null first mapping" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(null)
		thrown.getMessage must (include("arg=firstMapping") and include("null"))
	}

	it must "not accept any null mappings" in spinalContext {
		val otherMappingsWithNull = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsWithNull:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null"))
	}

	private def dummyMapping() = stubMappingWithMaster(dummyMaster())

	private def dummyMaster() = WishboneTestDoubles.stubMasterWith(dummyConfig)

	private def stubMappingWithMaster(master: Wishbone) = (master, (bus: Wishbone) => False, dummySlave())

	private def dummySlave() = WishboneTestDoubles.stubSlaveWith(dummyConfig)

	it must "not accept a null master in the first mapping" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithMaster(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("master"))
	}

	it must "not accept a null selector in the first mapping" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSelector(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	private def stubMappingWithSelector(selector: Wishbone => Bool) = (dummyMaster(), selector, dummySlave())

	it must "not accept a null selector result in the first mapping" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSelector(_ => null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("selector"))
	}

	it must "not accept a null slave in the first mapping" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(stubMappingWithSlave(null))
		thrown.getMessage must (include("arg=firstMapping") and include("null") and include("slave"))
	}

	private def stubMappingWithSlave(slave: Wishbone) = (dummyMaster(), (bus: Wishbone) => False, slave)

	it must "not accept a null master in any of the mappings" in spinalContext {
		val otherMappingsContainingNullMaster = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithMaster(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullMaster:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("master"))
	}

	it must "not accept a null selector in any of the mappings" in spinalContext {
		val otherMappingsContainingNullSelector = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelector:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null selector result in any of the mappings" in spinalContext {
		val otherMappingsContainingNullSelectorResult = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSelector(_ => null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSelectorResult:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("selector"))
	}

	it must "not accept a null slave in any of the mappings" in spinalContext {
		val otherMappingsContainingNullSlave = Random.shuffle(Seq.fill(3) { dummyMapping() } :+ stubMappingWithSlave(null))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(dummyMapping(), otherMappingsContainingNullSlave:_*)
		thrown.getMessage must (include("arg=otherMappings") and include("null") and include("slave"))
	}

	it must "not accept any masters that have different bus configurations" in spinalContext {
		val differingMaster = WishboneTestDoubles.stubMasterWith(WishboneConfigTestDoubles.stubDifferentTo(dummyConfig))
		val mastersContainingDifferences = Random.shuffle(dummyMasters() :+ differingMaster)
		val mappings = mastersContainingDifferences.map(master => (master, (bus: Wishbone) => True, dummySlave()))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(mappings.head, mappings.tail:_*)
		thrown.getMessage must (include("arg=busMap.masters") and include("identical"))
	}

	private def dummyMasters() = Seq.fill(anyNumberOfMasters()) { dummyMaster() }

	private def anyNumberOfMasters() = Random.between(1, 64)

	it must "not accept any slaves that have different bus configurations" in spinalContext {
		val differingSlave = WishboneTestDoubles.stubSlaveWith(WishboneConfigTestDoubles.stubDifferentTo(dummyConfig))
		val slavesContainingDifferences = Random.shuffle(dummySlaves() :+ differingSlave)
		val mappings = slavesContainingDifferences.map(slave => (dummyMaster(), (bus: Wishbone) => True, slave))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(mappings.head, mappings.tail:_*)
		thrown.getMessage must (include("arg=busMap.slaves") and include("identical"))
	}

	private def dummySlaves() = Seq.fill(anyNumberOfSlaves()) { dummySlave() }

	private def anyNumberOfSlaves() = Random.between(1, 64)

	it must "not accept any difference in bus configurations between masters and slaves" in spinalContext {
		val differentConfig = WishboneConfigTestDoubles.stubDifferentTo(dummyConfig)
		val slavesWithNonMasterConfig = Seq.fill(Random.between(1, 10)) { WishboneTestDoubles.stubSlaveWith(differentConfig) }
		val mappings = slavesWithNonMasterConfig.map(slave => (dummyMaster(), (bus: Wishbone) => True, slave))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterSlaveMap(mappings.head, mappings.tail:_*)
		thrown.getMessage must (include("arg=busMap.slaves") and include("identical"))
	}

	it must "hard-code any unmapped master-slave selection to false" in spinalContext {
		val masters = Seq(dummyMaster(), dummyMaster())
		val slaves = Seq(dummySlave(), dummySlave())
		val map = WishboneBusMasterSlaveMap(
			(masters(0), m => m.ADR =/= 0, slaves(0)),
			(masters(1), m => m.ADR =/= 0, slaves(1)))

		map.io.masters(0).slaveSelects(1).hasAssignement must be(true)
	}
}
