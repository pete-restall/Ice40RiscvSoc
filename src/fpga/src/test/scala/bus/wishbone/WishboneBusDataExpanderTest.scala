package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusDataExpander
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusDataExpanderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks with Inspectors {
	"WishboneBusDataExpander" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val expander = new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), anyNumberOfSlaves())
		expander.io.name must be("")
	}

	private def anyNumberOfSlaves() = Random.between(1, 32)

	it must "not accept null slave configuration" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusDataExpander(null, 1))
		thrown.getMessage must (include("arg=slaveConfig") and include("null"))
	}

	private val lessThanOneNumberOfSlaves = tableFor("numberOfSlaves", List(0, -1, -2, -33, -1234))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "not accept less than 1 slave" in spinalContext { () =>
		forAll(lessThanOneNumberOfSlaves) { (numberOfSlaves: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), numberOfSlaves))
			thrown.getMessage must include("arg=numberOfSlaves")
		}}
	}

	private val numberOfSlaves = tableFor("numberOfSlaves", List(1, 2, 3, anyNumberOfSlaves()))

	it must "have IO for the number of slaves passed to the constructor" in spinalContext { () =>
		forAll(numberOfSlaves) { (numberOfSlaves: Int) => {
			val expander = new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), numberOfSlaves)
			expander.io.slaves.length must be(numberOfSlaves)
		}}
	}

	it must "have the same Wishbone configuration for all slaves as passed to the constructor" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.dummy()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		forAll(expander.io.slaves) { slave => slave.config must equal(slaveConfig) }
	}

	it must "drive all slaves as a master" in spinalContext { () =>
		val expander = new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), anyNumberOfSlaves())
		forAll(expander.io.slaves) { slave => slave.isMasterInterface must be(true) }
	}

	it must "have different slave instances" in spinalContext { () =>
		val expander = new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), atLeastTwoSlaves())
		expander.io.slaves.distinct.length must be(expander.io.slaves.length)
	}

	private def atLeastTwoSlaves() = Random.between(2, 32)

	it must "be a slave to a master" in spinalContext { () =>
		val expander = new WishboneBusDataExpander(WishboneConfigTestDoubles.dummy(), anyNumberOfSlaves())
		expander.io.master.isMasterInterface must be(false)
	}

	it must "have a master address width equal to the slave address width" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		expander.io.master.ADR.getWidth must be(slaveConfig.addressWidth)
	}

	it must "have a master MISO data width equal to the sum of all slave MISO data widths" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		val totalSlaveDataWidth = totalOf(expander.io.slaves)(slave => slave.DAT_MISO.getWidth)
		expander.io.master.DAT_MISO.getWidth must be(totalSlaveDataWidth)
	}

	private def totalOf[A](items: Seq[A])(property: A => Int) = items.foldLeft(0)((acc, item) => acc + property(item))

	it must "have a master MOSI data width equal to the sum of all slave MOSI data widths" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		val totalSlaveDataWidth = totalOf(expander.io.slaves)(slave => slave.DAT_MOSI.getWidth)
		expander.io.master.DAT_MOSI.getWidth must be(totalSlaveDataWidth)
	}

	it must "have no master SEL when the slaves do not use SEL" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stubWith(selWidth=0)
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		expander.io.master.SEL must be(null)
	}

	it must "have a master SEL width equal to the sum of all slave SEL widths" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stubWith(selWidth=Random.between(1, 64))
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		val totalSlaveSelWidth = totalOf(expander.io.slaves)(slave => slave.SEL.getWidth)
		expander.io.master.SEL.getWidth must be(totalSlaveSelWidth)
	}

	private val booleans = tableFor("value", List(true, false))

	it must "expose STALL to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useSTALL=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useSTALL must be(value)
		}}
	}

	it must "expose LOCK to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useLOCK=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useLOCK must be(value)
		}}
	}

	it must "expose ERR to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useERR=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useERR must be(value)
		}}
	}

	it must "expose RTY to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useRTY=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useRTY must be(value)
		}}
	}

	it must "expose CTI to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useCTI=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useCTI must be(value)
		}}
	}

	it must "have a master TGA width equal to the slaves" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		expander.io.master.config.tgaWidth must be(slaveConfig.tgaWidth)
	}

	it must "have a master TGC width equal to the slaves" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		expander.io.master.config.tgcWidth must be(slaveConfig.tgcWidth)
	}

	it must "have a master TGD width equal to the slaves" in spinalContext { () =>
		val slaveConfig = WishboneConfigTestDoubles.stub()
		val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
		expander.io.master.config.tgdWidth must be(slaveConfig.tgdWidth)
	}

	it must "expose BTE to the master if used by the slaves" in spinalContext { () =>
		forAll(booleans) { (value: Boolean) => {
			val slaveConfig = WishboneConfigTestDoubles.stubWith(useBTE=value)
			val expander = new WishboneBusDataExpander(slaveConfig, anyNumberOfSlaves())
			expander.io.master.config.useBTE must be(value)
		}}
	}

	"WishboneBusDataExpander companion's apply() method" must "not accept a null first slave" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusDataExpander(null)
		thrown.getMessage must (include("arg=firstSlave") and include("null"))
	}

	private def dummySlave() = stubSlaveWith(WishboneConfigTestDoubles.dummy())

	private def stubSlaveWith(config: WishboneConfig) = new Wishbone(config)

	it must "not accept any null slaves" in spinalContext { () =>
		val otherSlavesWithNull = Random.shuffle(Seq.fill(3) { dummySlave() } :+ null)
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusDataExpander(dummySlave(), otherSlavesWithNull:_*)
		thrown.getMessage must (include("arg=otherSlaves") and include("null"))
	}

	it must "not accept any slave with a different configuration" in spinalContext { () =>
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = WishboneConfigTestDoubles.stubDifferentTo(firstConfig)
		val slaves = Random.shuffle(Seq.fill(10) { stubSlaveWith(firstConfig) } :+ stubSlaveWith(secondConfig))
		val thrown = the [IllegalArgumentException] thrownBy WishboneBusDataExpander(slaves.head, slaves.tail:_*)
		thrown.getMessage must (include("arg=otherSlaves") and include("same configuration"))
	}

	it must "compare slave configurations by value and not by reference" in spinalContext { () =>
		val firstConfig = WishboneConfigTestDoubles.stub()
		val secondConfig = firstConfig.copy()
		val slaves = Random.shuffle(Seq.fill(10) { stubSlaveWith(firstConfig) } :+ stubSlaveWith(secondConfig))
		noException must be thrownBy(WishboneBusDataExpander(slaves.head, slaves.tail:_*))
	}
}
