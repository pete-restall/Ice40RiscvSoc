package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusDataExpander
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class WishboneBusDataExpanderSimulationTest(slaveConfig: WishboneConfig, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusDataExpanderFixture]
	with TableDrivenPropertyChecks
	with Inspectors {

	protected override def dutFactory() = new WishboneBusDataExpanderFixture(slaveConfig, numberOfSlaves, dutCreatedViaApplyFactory)

	"WishboneBusDataExpander slaves" must "all have the same ADR as the master" in simulator { fixture =>
		fixture.io.master.ADR #= fixture.anyAddress()
		sleep(1)
		forAll(fixture.io.slaves) { slave => slave.ADR.toLong must be(fixture.io.master.ADR.toLong) }
	}

	private val booleans = Seq(true, false).asTable("value")

	they must "all have the same CYC as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.CYC #= value
			sleep(1)
			forAll(fixture.io.slaves) { slave => slave.CYC.toBoolean must be(fixture.io.master.CYC.toBoolean) }
		}
	}

	they must "all have the same STB as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.STB #= value
			sleep(1)
			forAll(fixture.io.slaves) { slave => slave.STB.toBoolean must be(fixture.io.master.STB.toBoolean) }
		}
	}

	they must "all have the same WE as the master" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) =>
			fixture.io.master.WE #= value
			sleep(1)
			forAll(fixture.io.slaves) { slave => slave.WE.toBoolean must be(fixture.io.master.WE.toBoolean) }
		}
	}

	they must "all have a slice of the master MOSI" in simulator { fixture =>
		val data = fixture.anyMasterData()
		val slices = slaveDataSliceLsbOffsets.map(i => (data >> i) & slaveDataSliceMask)
		fixture.io.master.DAT_MOSI #= data
		sleep(1)
		forAll(fixture.io.slaves.zip(slices)) { x => x._1.DAT_MOSI.toLong must be(x._2) }
	}

	private val slaveDataSliceLsbOffsets = steppedRange(numberOfSlaves, slaveConfig.dataWidth)

	private def steppedRange(numberOfSteps: Int, step: Int) = 0 until (numberOfSteps * step) by step

	private val slaveDataSliceMask = (1l << slaveConfig.dataWidth) - 1

	they must "all have a slice of the master SEL" in simulator { fixture =>
		val sel = fixture.anyMasterDataSelect()
		val slices = slaveDataSelectSliceLsbOffsets.map(i => (sel >> i) & slaveDataSelectSliceMask)
		fixture.io.master.SEL #= sel
		sleep(1)
		forAll(fixture.io.slaves.zip(slices)) { x => x._1.SEL.toLong must be(x._2) }
	}

	private val slaveDataSelectSliceLsbOffsets = steppedRange(numberOfSlaves, slaveConfig.selWidth)

	private val slaveDataSelectSliceMask = (1l << slaveConfig.selWidth) - 1

	"WishboneBusDataExpander master" must "have inactive ACK when all slaves have inactive ACK" in simulator { fixture =>
		fixture.io.slaves.foreach(slave => slave.ACK #= false)
		sleep(1)
		fixture.io.master.ACK.toBoolean must be(false)
	}

	it must "have inactive ACK when all slaves except one have active ACK" in simulator { fixture =>
		fixture.io.slaves.foreach(slave => slave.ACK #= true)
		anySlaveIn(fixture).ACK #= false
		sleep(1)
		fixture.io.master.ACK.toBoolean must be(false)
	}

	private def anySlaveIn(fixture: WishboneBusDataExpanderFixture) = fixture.io.slaves(Random.nextInt(fixture.io.slaves.length))

	it must "have active ACK when all slaves have active ACK" in simulator { fixture =>
		fixture.io.slaves.foreach(slave => slave.ACK #= true)
		sleep(1)
		fixture.io.master.ACK.toBoolean must be(true)
	}

	it must "have MISO as concatenation of all slave MISOs" in simulator { fixture =>
		fixture.io.slaves.foreach(slave => slave.DAT_MISO #= fixture.anySlaveData())
		sleep(1)
		fixture.io.master.DAT_MISO.toBigInt must be(concatenatedMisoFor(fixture.io.slaves))
	}

	private def concatenatedMisoFor(slaves: Seq[Wishbone]) = slaves.foldLeft((0, BigInt(0)))((acc, slave) => (
		acc._1 + slave.DAT_MISO.getWidth,
		acc._2 | (slave.DAT_MISO.toBigInt << acc._1)))._2

	it must "have null ERR when slaves have null ERR" in simulator { fixture =>
		if (!slaveConfig.useERR) {
			fixture.io.master.ERR must be(null)
		}
	}

	it must "have inactive ERR when all slaves have inactive ERR" in simulator { fixture =>
		if (slaveConfig.useERR) {
			fixture.io.slaves.foreach(slave => slave.ERR #= false)
			sleep(1)
			fixture.io.master.ERR.toBoolean must be(false)
		}
	}

	it must "have active ERR when all slaves have active ERR" in simulator { fixture =>
		if (slaveConfig.useERR) {
			fixture.io.slaves.foreach(slave => slave.ERR #= true)
			sleep(1)
			fixture.io.master.ERR.toBoolean must be(true)
		}
	}

	it must "have active ERR when one slave has active ERR" in simulator { fixture =>
		if (slaveConfig.useERR) {
			fixture.io.slaves.foreach(slave => slave.ERR #= false)
			anySlaveIn(fixture).ERR #= true
			sleep(1)
			fixture.io.master.ERR.toBoolean must be(true)
		}
	}

	it must "have null STALL when slaves have null STALL" in simulator { fixture =>
		if (!slaveConfig.useSTALL) {
			fixture.io.master.STALL must be(null)
		}
	}

	it must "have inactive STALL when all slaves have inactive STALL" in simulator { fixture =>
		if (slaveConfig.useSTALL) {
			fixture.io.slaves.foreach(slave => slave.STALL #= false)
			sleep(1)
			fixture.io.master.STALL.toBoolean must be(false)
		}
	}

	it must "have active STALL when all slaves have active STALL" in simulator { fixture =>
		if (slaveConfig.useSTALL) {
			fixture.io.slaves.foreach(slave => slave.STALL #= true)
			sleep(1)
			fixture.io.master.STALL.toBoolean must be(true)
		}
	}

	it must "have active STALL when one slave has active STALL" in simulator { fixture =>
		if (slaveConfig.useSTALL) {
			fixture.io.slaves.foreach(slave => slave.STALL #= false)
			anySlaveIn(fixture).STALL #= true
			sleep(1)
			fixture.io.master.STALL.toBoolean must be(true)
		}
	}

	it must "have null RTY when slaves have null RTY" in simulator { fixture =>
		if (!slaveConfig.useRTY) {
			fixture.io.master.RTY must be(null)
		}
	}

	it must "have inactive RTY when all slaves have inactive RTY" in simulator { fixture =>
		if (slaveConfig.useRTY) {
			fixture.io.slaves.foreach(slave => slave.RTY #= false)
			sleep(1)
			fixture.io.master.RTY.toBoolean must be(false)
		}
	}

	it must "have active RTY when all slaves have active RTY" in simulator { fixture =>
		if (slaveConfig.useRTY) {
			fixture.io.slaves.foreach(slave => slave.RTY #= true)
			sleep(1)
			fixture.io.master.RTY.toBoolean must be(true)
		}
	}

	it must "have active RTY when one slave has active RTY" in simulator { fixture =>
		if (slaveConfig.useRTY) {
			fixture.io.slaves.foreach(slave => slave.RTY #= false)
			anySlaveIn(fixture).RTY #= true
			sleep(1)
			fixture.io.master.RTY.toBoolean must be(true)
		}
	}
}
