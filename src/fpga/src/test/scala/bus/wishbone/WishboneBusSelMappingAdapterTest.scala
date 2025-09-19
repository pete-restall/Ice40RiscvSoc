package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusSelMappingAdapter
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusSelMappingAdapterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusSelMappingAdapter" must "not use the 'io' prefix for signals" in spinalContext {
		val adapter = new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), dummySelWidth(), dummyMapper)
		adapter.io.name must be("")
	}

	private def dummySelWidth() = 1 bit

	private def dummyMapper(sel: Bits) = B(0)

	it must "not accept a null master bus configuration" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSelMappingAdapter(null, dummySelWidth(), dummyMapper))
		thrown.getMessage must (include("arg=masterConfig") and include("null"))
	}

	it must "not accept a null slaveSelWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), null, dummyMapper))
		thrown.getMessage must (include("arg=slaveSelWidth") and include("null"))
	}

	private val lessThanZero = Seq(-1 bits, -2 bits, -33 bits, -1234 bits).asTable("invalidSelWidth")

	it must "not accept a slaveSelWidth value less than 0" in spinalContext {
		forAll(lessThanZero) { (invalidSelWidth: BitCount) =>
			val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), invalidSelWidth, dummyMapper))
			thrown.getMessage must include("arg=slaveSelWidth")
		}
	}

	it must "not accept a null slaveSelMapper" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), dummySelWidth(), null))
		thrown.getMessage must (include("arg=slaveSelMapper") and include("null"))
	}

	it must "not accept a null slaveSelMapper that returns null" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), dummySelWidth(), _ => null))
		thrown.getMessage must (include("arg=slaveSelMapper") and include("null"))
	}

	it must "not call slaveSelMapper when slaveSelWidth is zero" in spinalContext {
		val selMapper = (_: Bits) => fail("The slaveSelMapper function must not be called when SEL is 0 bits wide (ie. null)")
		val _ = new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), 0 bits, selMapper)
	}

	it must "have the same Wishbone configuration for the master as passed to the constructor" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = new WishboneBusSelMappingAdapter(masterConfig, dummySelWidth(), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}

	it must "have the same Wishbone configuration for the slave as passed to the constructor, with the amended selWidth" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(selWidth=anySelWidthExcept(masterConfig.selWidth))
		val adapter = new WishboneBusSelMappingAdapter(masterConfig, slaveConfig.selWidth bits, dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	private def anySelWidthExcept(except: Int): Int = {
		val selWidth = Random.between(0, 129)
		if (selWidth != except) selWidth else anySelWidthExcept(except)
	}

	it must "drive the slave as a master" in spinalContext {
		val adapter = new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), dummySelWidth(), dummyMapper)
		adapter.io.slave.isMasterInterface must be(true)
	}

	it must "be a slave to a master" in spinalContext {
		val adapter = new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), dummySelWidth(), dummyMapper)
		adapter.io.master.isMasterInterface must be(false)
	}

	"WishboneBusSelMappingAdapter slave" must "have a null SEL when slaveSelWidth is zero" in spinalContext {
		val adapter = new WishboneBusSelMappingAdapter(WishboneConfigTestDoubles.dummy(), 0 bits, dummyMapper)
		adapter.io.slave.SEL must be(null)
	}

	"WishboneBusSelMappingAdapter companion's apply(master, slave, mapper) method" must "not accept a null master" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(null.asInstanceOf[Wishbone], dummySlave(), dummyMapper)
		thrown.getMessage must (include("arg=master") and include("null"))
	}

	private def dummySlave() = stubSlaveWith(WishboneConfigTestDoubles.dummy())

	private def stubSlaveWith(config: WishboneConfig) = stubWishboneWith(config)

	private def stubWishboneWith(config: WishboneConfig) = new Wishbone(config)

	it must "not accept a null slave" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(dummyMaster(), null.asInstanceOf[Wishbone], dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("null"))
	}

	private def dummyMaster() = stubMasterWith(WishboneConfigTestDoubles.dummy())

	private def stubMasterWith(config: WishboneConfig) = stubWishboneWith(config)

	it must "not accept a null slaveSelMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(master, stubSlaveWith(master.config), null)
		thrown.getMessage must (include("arg=slaveSelMapper") and include("null"))
	}

	it must "not accept a slave with a different configuration to the master, except for selWidth" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val master = stubMasterWith(masterConfig)
		val slaveConfig = stubDifferentConfigTo(masterConfig)
		val slave = stubSlaveWith(slaveConfig)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(master, slave, dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("same configuration"))
	}

	private def stubDifferentConfigTo(config: WishboneConfig): WishboneConfig = {
		val differentConfig = WishboneConfigTestDoubles.stubDifferentTo(config).copy(selWidth=config.selWidth)
		if (config != differentConfig) differentConfig else stubDifferentConfigTo(config)
	}

	it must "compare bus configurations by value and not by reference" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy()
		noException must be thrownBy(WishboneBusSelMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper))
	}

	it must "use the given Wishbone configuration for the master" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(selWidth=Random.between(0, 128))
		val adapter = WishboneBusSelMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}

	it must "use the given Wishbone configuration for the slave" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(selWidth=Random.between(0, 128))
		val adapter = WishboneBusSelMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	"WishboneBusSelMappingAdapter companion's apply(masterSelWidth, slave, mapper) method" must "not accept a null masterSelWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(null.asInstanceOf[BitCount], dummySlave(), dummyMapper)
		thrown.getMessage must (include("arg=masterSelWidth") and include("null"))
	}

	it must "not accept a null slave" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(dummySelWidth(), null, dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("null"))
	}

	it must "not accept a null slaveSelMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(dummySelWidth(), dummySlave(), null)
		thrown.getMessage must (include("arg=slaveSelMapper") and include("null"))
	}

	it must "use the given slave's Wishbone configuration for the master, with the given masterSelWidth" in spinalContext {
		val masterSelWidth = Random.between(0, 128)
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusSelMappingAdapter(masterSelWidth bits, stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.master.config must equal(slaveConfig.copy(selWidth=masterSelWidth))
	}

	it must "use the given slave's Wishbone configuration for the slave" in spinalContext {
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusSelMappingAdapter(dummySelWidth(), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	"WishboneBusSelMappingAdapter companion's apply(master, slaveSelWidth, mapper) method" must "not accept a null master" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(null, dummySelWidth(), dummyMapper)
		thrown.getMessage must (include("arg=master") and include("null"))
	}

	it must "not accept a null slaveSelWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(dummyMaster(), null.asInstanceOf[BitCount], dummyMapper)
		thrown.getMessage must (include("arg=slaveSelWidth") and include("null"))
	}

	it must "not accept a null slaveSelMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSelMappingAdapter(dummyMaster(), dummySelWidth(), null)
		thrown.getMessage must (include("arg=slaveSelMapper") and include("null"))
	}

	it must "use the given master's Wishbone configuration for the slave, with the given slaveSelWidth" in spinalContext {
		val slaveSelWidth = Random.between(0, 128)
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusSelMappingAdapter(stubMasterWith(masterConfig), slaveSelWidth bits, dummyMapper)
		adapter.io.slave.config must equal(masterConfig.copy(selWidth=slaveSelWidth))
	}

	it must "use the given master's Wishbone configuration for the master" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusSelMappingAdapter(stubMasterWith(masterConfig), dummySelWidth(), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}
}
