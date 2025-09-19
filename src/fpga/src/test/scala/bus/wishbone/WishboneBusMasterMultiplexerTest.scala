package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusMasterMultiplexer
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusMasterMultiplexerTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusMasterMultiplexer" must "not use the 'io' prefix for signals" in spinalContext {
		val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), anyNumberOfMasters())
		mux.io.name must be("")
	}

	private def anyNumberOfMasters() = Random.between(1, 32)

	it must "not accept null bus configuration" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterMultiplexer(null, 1))
		thrown.getMessage must (include("arg=busConfig") and include("null"))
	}

	private val lessThanOneNumberOfMasters = Seq(0, -1, -2, -33, -1234).asTable("numberOfMasters")

	it must "not accept less than 1 master" in spinalContext {
		forAll(lessThanOneNumberOfMasters) { (numberOfMasters: Int) =>
			val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfMasters))
			thrown.getMessage must include("arg=numberOfMasters")
		}
	}

	private val numberOfMasters = Seq(1, 2, 3, anyNumberOfMasters()).asTable("numberOfMasters")

	it must "have IO for the number of masters passed to the constructor" in spinalContext {
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfMasters)
			mux.io.masters.length must be(numberOfMasters)
		}
	}

	it must "have the same Wishbone configuration for the slave as passed to the constructor" in spinalContext {
		val busConfig = WishboneConfigTestDoubles.dummy()
		val mux = new WishboneBusMasterMultiplexer(busConfig, anyNumberOfMasters())
		mux.io.slave.config must equal(busConfig)
	}

	it must "have the same Wishbone configuration for all masters as passed to the constructor" in spinalContext {
		val busConfig = WishboneConfigTestDoubles.dummy()
		val mux = new WishboneBusMasterMultiplexer(busConfig, anyNumberOfMasters())
		forAll(mux.io.masters) { master => master.config must equal(busConfig) }
	}

	it must "drive the slave as a master" in spinalContext {
		val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), anyNumberOfMasters())
		mux.io.slave.isMasterInterface must be(true)
	}

	it must "have different master instances" in spinalContext {
		val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), atLeastTwoMasters())
		mux.io.masters.distinct.length must be(mux.io.masters.length)
	}

	private def atLeastTwoMasters() = Random.between(2, 32)

	it must "be a slave to the masters" in spinalContext {
		val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), anyNumberOfMasters())
		forAll(mux.io.masters) { master => master.isMasterInterface must be(false) }
	}

	private val sufficientlySizedSelectors = Seq(
		(1, 1 bit),
		(2, 1 bit),
		(3, 2 bits),
		(4, 2 bits),
		(5, 3 bits),
		(8, 3 bits),
		(31, 5 bits),
		(32, 5 bits),
		(33, 6 bits),
		(65, 7 bits),
		(128, 7 bits)
	).asTable("numberOfMasters", "selectorWidth")

	it must "have a selector of adequate width for the number of masters" in spinalContext{ () =>
		forAll(sufficientlySizedSelectors) { (numberOfMasters: Int, selectorWidth: BitCount) =>
			val mux = new WishboneBusMasterMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfMasters)
			mux.io.selector.getWidth must be(selectorWidth.value)
		}
	}

	"WishboneBusMasterMultiplexer companion's apply() method" must "not accept a null selector" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(null, dummyMaster())
		thrown.getMessage must (include("arg=selector") and include("null"))
	}

	private def dummyMaster() = stubMasterWith(WishboneConfigTestDoubles.dummy())

	private def stubMasterWith(config: WishboneConfig) = new Wishbone(config)

	private val narrowSelectors = Seq(
		(3, 1 bit),
		(4, 1 bit),
		(5, 1 bit),
		(5, 2 bits),
		(6, 2 bits),
		(9, 3 bits),
		(65, 6 bits),
		(129, 7 bits)
	).asTable("numberOfMasters", "selectorWidth")

	it must "not accept a selector that is not wide enough for the given number of masters" in spinalContext {
		forAll(narrowSelectors) { (numberOfMasters: Int, selectorWidth: BitCount) =>
			val narrowSelector = UInt(selectorWidth)
			val masters = stubMastersWithSameConfig(numberOfMasters)
			val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(narrowSelector, masters.head, masters.tail:_*)
			thrown.getMessage must (include("arg=selector") and include("expectedWidth"))
		}
	}

	private def stubMastersWithSameConfig(numberOfMasters: Int) = {
		val config = WishboneConfigTestDoubles.stub()
		Seq.fill(numberOfMasters) { stubMasterWith(config) }
	}

	private val wideSelectors = Seq(
		(1, 2 bits),
		(2, 2 bits),
		(3, 3 bits),
		(4, 3 bits),
		(5, 4 bits),
		(5, 8 bits),
		(16, 5 bits),
		(128, 8 bits),
		(128, 16 bits)
	).asTable("numberOfMasters", "selectorWidth")

	it must "not accept a selector that is too wide for the given number of masters" in spinalContext {
		forAll(wideSelectors) { (numberOfMasters: Int, selectorWidth: BitCount) =>
			val wideSelector = UInt(selectorWidth)
			val masters = stubMastersWithSameConfig(numberOfMasters)
			val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(wideSelector, masters.head, masters.tail:_*)
			thrown.getMessage must (include("arg=selector") and include("expectedWidth"))
		}
	}

	it must "not accept a null first master" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(stubSelectorFor(1), null)
		thrown.getMessage must (include("arg=firstMaster") and include("null"))
	}

	private def stubSelectorFor(numberOfMasters: Int) = UInt(Math.max(1, log2Up(numberOfMasters)) bits)

	it must "not accept any null masters" in spinalContext {
		val otherMastersWithNull = Random.shuffle(Seq.fill(3) { dummyMaster() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(stubSelectorFor(4), dummyMaster(), otherMastersWithNull:_*)
		thrown.getMessage must (include("arg=otherMasters") and include("null"))
	}

	it must "not accept any master with a different configuration" in spinalContext {
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = WishboneConfigTestDoubles.stubDifferentTo(firstConfig)
		val masters = Random.shuffle(Seq.fill(10) { stubMasterWith(firstConfig) } :+ stubMasterWith(secondConfig))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusMasterMultiplexer(stubSelectorFor(10), masters.head, masters.tail:_*)
		thrown.getMessage must (include("arg=otherMasters") and include("same configuration"))
	}

	it must "compare bus configurations by value and not by reference" in spinalContext {
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = firstConfig.copy()
		val masters = Random.shuffle(Seq.fill(10) { stubMasterWith(firstConfig) } :+ stubMasterWith(secondConfig))
		noException must be thrownBy(WishboneBusMasterMultiplexer(stubSelectorFor(10), masters.head, masters.tail:_*))
	}

	it must "use the same Wishbone configuration for the slave as the masters" in spinalContext {
		val masters = stubMastersWithSameConfig(Random.between(1, 10))
		val mux = WishboneBusMasterMultiplexer(stubSelectorFor(masters.length), masters.head, masters.tail:_*)
		mux.io.slave.config must equal(masters.head.config)
	}

	it must "have IO for each of the masters passed to the constructor" in spinalContext {
		forAll(numberOfMasters) { (numberOfMasters: Int) =>
			val masters = stubMastersWithSameConfig(numberOfMasters)
			val mux = WishboneBusMasterMultiplexer(stubSelectorFor(masters.length), masters.head, masters.tail:_*)
			mux.io.masters.length must be(numberOfMasters)
		}
	}
}
