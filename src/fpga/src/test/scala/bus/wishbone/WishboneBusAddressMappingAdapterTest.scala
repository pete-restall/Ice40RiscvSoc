package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusAddressMappingAdapter
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusAddressMappingAdapterTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusAddressMappingAdapter" must "not use the 'io' prefix for signals" in spinalContext {
		val adapter = new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), dummyAddressWidth(), dummyMapper)
		adapter.io.name must be("")
	}

	private def dummyAddressWidth() = 1 bit

	private def dummyMapper(adr: UInt) = U(0)

	it must "not accept a null master bus configuration" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusAddressMappingAdapter(null, dummyAddressWidth(), dummyMapper))
		thrown.getMessage must (include("arg=masterConfig") and include("null"))
	}

	it must "not accept a null slaveAddressWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), null, dummyMapper))
		thrown.getMessage must (include("arg=slaveAddressWidth") and include("null"))
	}

	private val lessThanZero = Seq(-1 bits, -2 bits, -33 bits, -1234 bits).asTable("invalidAddressWidth")

	it must "not accept a slaveAddressWidth value less than 0" in spinalContext {
		forAll(lessThanZero) { (invalidAddressWidth: BitCount) =>
			val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), invalidAddressWidth, dummyMapper))
			thrown.getMessage must include("arg=slaveAddressWidth")
		}
	}

	it must "not accept a null slaveAddressMapper" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), dummyAddressWidth(), null))
		thrown.getMessage must (include("arg=slaveAddressMapper") and include("null"))
	}

	it must "not accept a null slaveAddressMapper that returns null" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), dummyAddressWidth(), _ => null))
		thrown.getMessage must (include("arg=slaveAddressMapper") and include("null"))
	}

	it must "not call slaveAddressMapper when slaveAddressWidth is zero" in spinalContext {
		val adrMapper = (_: UInt) => fail("The slaveAddressMapper function must not be called when ADR is 0 bits wide")
		val _ = new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), 0 bits, adrMapper)
	}

	it must "have the same Wishbone configuration for the master as passed to the constructor" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = new WishboneBusAddressMappingAdapter(masterConfig, dummyAddressWidth(), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}

	it must "have the same Wishbone configuration for the slave as passed to the constructor, with the amended addressWidth" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(addressWidth=anyAddressWidthExcept(masterConfig.addressWidth))
		val adapter = new WishboneBusAddressMappingAdapter(masterConfig, slaveConfig.addressWidth bits, dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	private def anyAddressWidthExcept(except: Int): Int = {
		val addressWidth = Random.between(0, 129)
		if (addressWidth != except) addressWidth else anyAddressWidthExcept(except)
	}

	it must "drive the slave as a master" in spinalContext {
		val adapter = new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), dummyAddressWidth(), dummyMapper)
		adapter.io.slave.isMasterInterface must be(true)
	}

	it must "be a slave to a master" in spinalContext {
		val adapter = new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), dummyAddressWidth(), dummyMapper)
		adapter.io.master.isMasterInterface must be(false)
	}

	"WishboneBusAddressMappingAdapter slave" must "not have a null ADR when slaveAddressWidth is zero" in spinalContext {
		val adapter = new WishboneBusAddressMappingAdapter(WishboneConfigTestDoubles.dummy(), 0 bits, dummyMapper)
		adapter.io.slave.ADR.getWidth must be(0)
	}

	"WishboneBusAddressMappingAdapter companion's apply(master, slave, mapper) method" must "not accept a null master" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(null.asInstanceOf[Wishbone], dummySlave(), dummyMapper)
		thrown.getMessage must (include("arg=master") and include("null"))
	}

	private def dummySlave() = stubSlaveWith(WishboneConfigTestDoubles.dummy())

	private def stubSlaveWith(config: WishboneConfig) = stubWishboneWith(config)

	private def stubWishboneWith(config: WishboneConfig) = new Wishbone(config)

	it must "not accept a null slave" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(dummyMaster(), null.asInstanceOf[Wishbone], dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("null"))
	}

	private def dummyMaster() = stubMasterWith(WishboneConfigTestDoubles.dummy())

	private def stubMasterWith(config: WishboneConfig) = stubWishboneWith(config)

	it must "not accept a null slaveAddressMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(master, stubSlaveWith(master.config), null)
		thrown.getMessage must (include("arg=slaveAddressMapper") and include("null"))
	}

	it must "not accept a slave with a different configuration to the master, except for addressWidth" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val master = stubMasterWith(masterConfig)
		val slaveConfig = stubDifferentConfigTo(masterConfig)
		val slave = stubSlaveWith(slaveConfig)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(master, slave, dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("same configuration"))
	}

	private def stubDifferentConfigTo(config: WishboneConfig): WishboneConfig = {
		val differentConfig = WishboneConfigTestDoubles.stubDifferentTo(config).copy(addressWidth=config.addressWidth)
		if (config != differentConfig) differentConfig else stubDifferentConfigTo(config)
	}

	it must "compare bus configurations by value and not by reference" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy()
		noException must be thrownBy(WishboneBusAddressMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper))
	}

	it must "use the given Wishbone configuration for the master" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(addressWidth=Random.between(0, 128))
		val adapter = WishboneBusAddressMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}

	it must "use the given Wishbone configuration for the slave" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val slaveConfig = masterConfig.copy(addressWidth=Random.between(0, 128))
		val adapter = WishboneBusAddressMappingAdapter(stubMasterWith(masterConfig), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	"WishboneBusAddressMappingAdapter companion's apply(masterAddressWidth, slave, mapper) method" must "not accept a null masterAddressWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(null.asInstanceOf[BitCount], dummySlave(), dummyMapper)
		thrown.getMessage must (include("arg=masterAddressWidth") and include("null"))
	}

	it must "not accept a null slave" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(dummyAddressWidth(), null, dummyMapper)
		thrown.getMessage must (include("arg=slave") and include("null"))
	}

	it must "not accept a null slaveAddressMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(dummyAddressWidth(), dummySlave(), null)
		thrown.getMessage must (include("arg=slaveAddressMapper") and include("null"))
	}

	it must "use the given slave's Wishbone configuration for the master, with the given masterAddressWidth" in spinalContext {
		val masterAddressWidth = Random.between(0, 128)
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusAddressMappingAdapter(masterAddressWidth bits, stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.master.config must equal(slaveConfig.copy(addressWidth=masterAddressWidth))
	}

	it must "use the given slave's Wishbone configuration for the slave" in spinalContext {
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusAddressMappingAdapter(dummyAddressWidth(), stubSlaveWith(slaveConfig), dummyMapper)
		adapter.io.slave.config must equal(slaveConfig)
	}

	"WishboneBusAddressMappingAdapter companion's apply(master, slaveAddressWidth, mapper) method" must "not accept a null master" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(null, dummyAddressWidth(), dummyMapper)
		thrown.getMessage must (include("arg=master") and include("null"))
	}

	it must "not accept a null slaveAddressWidth" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(dummyMaster(), null.asInstanceOf[BitCount], dummyMapper)
		thrown.getMessage must (include("arg=slaveAddressWidth") and include("null"))
	}

	it must "not accept a null slaveAddressMapper function" in spinalContext {
		val master = dummyMaster()
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusAddressMappingAdapter(dummyMaster(), dummyAddressWidth(), null)
		thrown.getMessage must (include("arg=slaveAddressMapper") and include("null"))
	}

	it must "use the given master's Wishbone configuration for the slave, with the given slaveAddressWidth" in spinalContext {
		val slaveAddressWidth = Random.between(0, 128)
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusAddressMappingAdapter(stubMasterWith(masterConfig), slaveAddressWidth bits, dummyMapper)
		adapter.io.slave.config must equal(masterConfig.copy(addressWidth=slaveAddressWidth))
	}

	it must "use the given master's Wishbone configuration for the master" in spinalContext {
		val masterConfig = WishboneConfigTestDoubles.stub()
		val adapter = WishboneBusAddressMappingAdapter(stubMasterWith(masterConfig), dummyAddressWidth(), dummyMapper)
		adapter.io.master.config must equal(masterConfig)
	}
}
