package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSlaveMultiplexer
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusSlaveMultiplexerTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusSlaveMultiplexer" must "not accept null bus configuration" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSlaveMultiplexer(null, 1))
		thrown.getMessage must (include("arg=busConfig") and include("null"))
	}

	private val lessThanOneNumberOfSlaves = tableFor("numberOfSlaves", List(0, -1, -2, -33, -1234))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "not accept less than 1 slave" in spinalContext { () =>
		forAll(lessThanOneNumberOfSlaves) { (numberOfSlaves: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfSlaves))
			thrown.getMessage must include("arg=numberOfSlaves")
		}}
	}

	private val numberOfSlaves = tableFor("numberOfSlaves", List(1, 2, 3, anyNumberOfSlaves()))

	private def anyNumberOfSlaves() = Random.between(1, 32)

	it must "have IO for the number of slaves passed to the constructor" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) => {
			val mux = new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfSlaves)
			mux.io.slaves.length must be(numberOfSlaves)
		}}
	}

	it must "have the same Wishbone configuration for the master as passed to the constructor" in spinalContext { () =>
		val busConfig = WishboneConfigTestDoubles.dummy()
		val mux = new WishboneBusSlaveMultiplexer(busConfig, anyNumberOfSlaves())
		mux.io.master.config must equal(busConfig)
	}

	it must "have the same Wishbone configuration for all slaves as passed to the constructor" in spinalContext { () =>
		val busConfig = WishboneConfigTestDoubles.dummy()
		val mux = new WishboneBusSlaveMultiplexer(busConfig, anyNumberOfSlaves())
		forAll(mux.io.slaves) { slave => slave.config must equal(busConfig) }
	}

	it must "drive all slaves as a master" in spinalContext { () =>
		val mux = new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), anyNumberOfSlaves())
		forAll(mux.io.slaves) { slave => slave.isMasterInterface must be(true) }
	}

	it must "have different slave instances" in spinalContext { () =>
		val mux = new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), atLeastTwoSlaves())
		mux.io.slaves.distinct.length must be(mux.io.slaves.length)
	}

	private def atLeastTwoSlaves() = Random.between(2, 32)

	it must "be a slave to a master" in spinalContext { () =>
		val mux = new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), anyNumberOfSlaves())
		mux.io.master.isMasterInterface must be(false)
	}

	private val sufficientlySizedSelectors = tableFor(("numberOfSlaves", "selectorWidth"), List(
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
		(128, 7 bits)))

	private def tableFor[A, B](header: (String, String), values: Iterable[(A, B)]) = Table(header) ++ values

	it must "have a selector of adequate width for the number of slaves" in spinalContext{ () =>
		forAll(sufficientlySizedSelectors) { (numberOfSlaves: Int, selectorWidth: BitCount) => {
			val mux = new WishboneBusSlaveMultiplexer(WishboneConfigTestDoubles.dummy(), numberOfSlaves)
			mux.io.selector.getWidth must be(selectorWidth.value)
		}}
	}

	"WishboneBusSlaveMultiplexer companion's apply() method" must "not accept a null selector" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(null, dummySlave())
		thrown.getMessage must (include("arg=selector") and include("null"))
	}

	private def dummySlave() = stubSlaveWith(WishboneConfigTestDoubles.dummy())

	private def stubSlaveWith(config: WishboneConfig) = new Wishbone(config)

	private val narrowSelectors = tableFor(("numberOfSlaves", "selectorWidth"), List(
		(3, 1 bit),
		(4, 1 bit),
		(5, 1 bit),
		(5, 2 bits),
		(6, 2 bits),
		(9, 3 bits),
		(65, 6 bits),
		(129, 7 bits)))

	it must "not accept a selector that is not wide enough for the given number of slaves" in spinalContext { () =>
		forAll(narrowSelectors) { (numberOfSlaves: Int, selectorWidth: BitCount) => {
			val narrowSelector = UInt(selectorWidth)
			val slaves = stubSlavesWithSameConfig(numberOfSlaves)
			val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(narrowSelector, slaves.head, slaves.tail:_*)
			thrown.getMessage must (include("arg=selector") and include("expectedWidth"))
		}}
	}

	private def stubSlavesWithSameConfig(numberOfSlaves: Int) = {
		val config = WishboneConfigTestDoubles.stub()
		Seq.fill(numberOfSlaves) { stubSlaveWith(config) }
	}

	private val wideSelectors = tableFor(("numberOfSlaves", "selectorWidth"), List(
		(1, 2 bits),
		(2, 2 bits),
		(3, 3 bits),
		(4, 3 bits),
		(5, 4 bits),
		(5, 8 bits),
		(16, 5 bits),
		(128, 8 bits),
		(128, 16 bits)))

	it must "not accept a selector that is too wide for the given number of slaves" in spinalContext { () =>
		forAll(wideSelectors) { (numberOfSlaves: Int, selectorWidth: BitCount) => {
			val wideSelector = UInt(selectorWidth)
			val slaves = stubSlavesWithSameConfig(numberOfSlaves)
			val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(wideSelector, slaves.head, slaves.tail:_*)
			thrown.getMessage must (include("arg=selector") and include("expectedWidth"))
		}}
	}

	it must "not accept a null first slave" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(stubSelectorFor(1), null)
		thrown.getMessage must (include("arg=firstSlave") and include("null"))
	}

	private def stubSelectorFor(numberOfSlaves: Int) = UInt(Math.max(1, log2Up(numberOfSlaves)) bits)

	it must "not accept any null slaves" in spinalContext { () =>
		val otherSlavesWithNull = Random.shuffle(Seq.fill(3) { dummySlave() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(stubSelectorFor(4), dummySlave(), otherSlavesWithNull:_*)
		thrown.getMessage must (include("arg=otherSlaves") and include("null"))
	}

	it must "not accept any slave with a different configuration" in spinalContext { () =>
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = WishboneConfigTestDoubles.stubDifferentTo(firstConfig)
		val slaves = Random.shuffle(Seq.fill(10) { stubSlaveWith(firstConfig) } :+ stubSlaveWith(secondConfig))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusSlaveMultiplexer(stubSelectorFor(10), slaves.head, slaves.tail:_*)
		thrown.getMessage must (include("arg=otherSlaves") and include("same configuration"))
	}

	it must "compare bus configurations by value and not by reference" in spinalContext { () =>
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = firstConfig.copy()
		val slaves = Random.shuffle(Seq.fill(10) { stubSlaveWith(firstConfig) } :+ stubSlaveWith(secondConfig))
		noException must be thrownBy(WishboneBusSlaveMultiplexer(stubSelectorFor(10), slaves.head, slaves.tail:_*))
	}

	it must "use the same Wishbone configuration as the slaves" in spinalContext { () =>
		val slaves = stubSlavesWithSameConfig(Random.between(1, 10))
		val mux = WishboneBusSlaveMultiplexer(stubSelectorFor(slaves.length), slaves.head, slaves.tail:_*)
		mux.io.master.config must equal(slaves.head.config)
	}

	it must "have IO for each of the slaves passed to the constructor" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) => {
			val slaves = stubSlavesWithSameConfig(numberOfSlaves)
			val mux = WishboneBusSlaveMultiplexer(stubSelectorFor(slaves.length), slaves.head, slaves.tail:_*)
			mux.io.slaves.length must be(numberOfSlaves)
		}}
	}
}
