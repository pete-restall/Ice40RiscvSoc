package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSlaveMultiplexer
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusSlaveMultiplexerSimulationTest(busConfig: WishboneConfig, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusSlaveMultiplexerFixture]
	with TableDrivenPropertyChecks
	with Inspectors {

	protected override def dutFactory() = new WishboneBusSlaveMultiplexerFixture(busConfig, numberOfSlaves, dutCreatedViaApplyFactory)

	"WishboneBusSlaveMultiplexer slaves" must "all have a non-multiplexed master ADR" in simulator { fixture =>
		fixture.io.master.ADR #= fixture.anyAddress()
		fixture.io.selector #= fixture.anySlaveIndex()
		sleep(1)
		forAll(fixture.io.slaves) { slave => slave.ADR.toLong must be(fixture.io.master.ADR.toLong) }
	}

	they must "all have a non-multiplexed master SEL" in simulator { fixture =>
		if (fixture.io.master.SEL != null) {
			fixture.io.master.SEL #= fixture.anySel()
			fixture.io.selector #= fixture.anySlaveIndex()
			sleep(1)
			forAll(fixture.io.slaves) { slave => slave.SEL.toInt must be(fixture.io.master.SEL.toInt) }
		}
	}

	they must "all have a non-multiplexed master DAT_MOSI" in simulator { fixture =>
		fixture.io.master.DAT_MOSI #= fixture.anyData()
		fixture.io.selector #= fixture.anySlaveIndex()
		sleep(1)
		forAll(fixture.io.slaves) { slave => slave.DAT_MOSI.toLong must be(fixture.io.master.DAT_MOSI.toLong) }
	}

	private val booleans = Seq(true, false).asTable("value")

	they must "all have a non-multiplexed master WE" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) => {
			fixture.io.master.WE #= value
			fixture.io.selector #= fixture.anySlaveIndex()
			sleep(1)
			forAll(fixture.io.slaves) { slave => slave.WE.toBoolean must be(value) }
		}}
	}

	they must "all have their DAT_MISO multiplexed to the master" in simulator { fixture =>
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		fixture.io.slaves.foreach(slave => slave.DAT_MISO #= fixture.anyData())
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= index
			sleep(1)
			fixture.io.master.DAT_MISO.toLong must be(slave.DAT_MISO.toLong)
		}
	}

	they must "only have true CYC when selected and the master CYC is also true" in simulator { fixture =>
		fixture.io.master.CYC #= true
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= index
			sleep(1)
			slave.CYC.toBoolean must be(true)
		}
	}

	they must "have false CYC when not selected and the master CYC is also true" in simulator { fixture =>
		fixture.io.master.CYC #= true
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= fixture.anySlaveIndexExcept(index)
			sleep(1)
			slave.CYC.toBoolean must be(false)
		}
	}

	they must "have false CYC when selected and the master CYC is also false" in simulator { fixture =>
		fixture.io.master.CYC #= false
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= index
			sleep(1)
			slave.CYC.toBoolean must be(false)
		}
	}

	they must "have false CYC when not selected and the master CYC is also false" in simulator { fixture =>
		fixture.io.master.CYC #= false
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= fixture.anySlaveIndexExcept(index)
			sleep(1)
			slave.CYC.toBoolean must be(false)
		}
	}

	they must "only have true STB when selected and the master STB is also true" in simulator { fixture =>
		fixture.io.master.STB #= true
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= index
			sleep(1)
			slave.STB.toBoolean must be(true)
		}
	}

	they must "have false STB when not selected and the master STB is also true" in simulator { fixture =>
		fixture.io.master.STB #= true
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= fixture.anySlaveIndexExcept(index)
			sleep(1)
			slave.STB.toBoolean must be(false)
		}
	}

	they must "have false STB when selected and the master STB is also false" in simulator { fixture =>
		fixture.io.master.STB #= false
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= index
			sleep(1)
			slave.STB.toBoolean must be(false)
		}
	}

	they must "have false STB when not selected and the master STB is also false" in simulator { fixture =>
		fixture.io.master.STB #= false
		val slavesInAnyOrder = Random.shuffle(fixture.io.slaves.zipWithIndex.toSeq)
		forAll(slavesInAnyOrder) { case(slave, index) =>
			fixture.io.selector #= fixture.anySlaveIndexExcept(index)
			sleep(1)
			slave.STB.toBoolean must be(false)
		}
	}

	"Multiplexed DAT_MISO" must "be zero for invalid slave indices" in simulator { implicit fixture =>
		forInvalidSlaves(master => master.DAT_MISO.toLong must be(0))((fixture, slave) => slave.DAT_MISO #= fixture.anyData())
	}

	private def forInvalidSlaves(expectation: Wishbone => Unit)(arrange: (WishboneBusSlaveMultiplexerFixture, Wishbone) => Unit)(implicit fixture: WishboneBusSlaveMultiplexerFixture) {
		val possibleNumberOfSlaves = fixture.io.selector.maxValue.toLong + 1
		if (fixture.io.slaves.length < possibleNumberOfSlaves) {
			val invalidSlavesInAnyOrder = Set(fixture.io.slaves.length, possibleNumberOfSlaves - 1) ++ Set.fill(10) { Random.between(fixture.io.slaves.length, possibleNumberOfSlaves) }
			fixture.io.slaves.foreach(slave => arrange(fixture, slave))
			forAll(invalidSlavesInAnyOrder) { index =>
				fixture.io.selector #= index
				sleep(1)
				expectation(fixture.io.master)
			}
		}
	}

	"Multiplexed ACK" must "be false for invalid slave indices when ERR is present and STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			forInvalidSlaves(master => master.ACK.toBoolean must be(false)) { (fixture, slave) =>
				fixture.io.master.STB #= false
				slave.ACK #= true
			}
		}
	}

	it must "be false for invalid slave indices when ERR is present and STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			forInvalidSlaves(master => master.ACK.toBoolean must be(false)) { (fixture, slave) =>
				fixture.io.master.STB #= true
				slave.ACK #= true
			}
		}
	}

	it must "be false for invalid slave indices when ERR is not present and STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.ERR == null) {
			forInvalidSlaves(master => master.ACK.toBoolean must be(false)) { (fixture, slave) =>
				fixture.io.master.STB #= false
				slave.ACK #= true
			}
		}
	}

	it must "be true for invalid slave indices when ERR is not present and STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.ERR == null) {
			forInvalidSlaves(master => master.ACK.toBoolean must be(true)) { (fixture, slave) =>
				fixture.io.master.STB #= true
				slave.ACK #= false
			}
		}
	}

	it must "follow the value of the selected slave's ACK when STB is false" in simulator { implicit fixture =>
		fixture.io.master.STB #= false
		mustFollowTheValueOfTheSelectedSlavesLine(_.ACK)
	}

	private def mustFollowTheValueOfTheSelectedSlavesLine(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusSlaveMultiplexerFixture) {
		forAll(fixture.io.slaves.zipWithIndex) { case(slave, index) =>
			fixture.io.selector #= index
			ioLineFrom(slave) #= false
			sleep(1)
			ioLineFrom(fixture.io.master).toBoolean must be(false)

			ioLineFrom(slave) #= true
			sleep(1)
			ioLineFrom(fixture.io.master).toBoolean must be(true)
		}
	}

	it must "follow the value of the selected slave's ACK when STB is true" in simulator { implicit fixture =>
		fixture.io.master.STB #= true
		mustFollowTheValueOfTheSelectedSlavesLine(_.ACK)
	}

	"Multiplexed ERR" must "be false for all invalid indices when STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			forInvalidSlaves(master => master.ERR.toBoolean must be(false)) { (fixture, slave) =>
				fixture.io.master.STB #= false
				slave.ERR #= true
			}
		}
	}

	it must "be true for all invalid indices when STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			forInvalidSlaves(master => master.ERR.toBoolean must be(true)) { (fixture, slave) =>
				fixture.io.master.STB #= true
				slave.ERR #= false
			}
		}
	}

	it must "follow the value of the selected slave's ERR when STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			fixture.io.master.STB #= false
			mustFollowTheValueOfTheSelectedSlavesLine(_.ERR)
		}
	}

	it must "follow the value of the selected slave's ERR when STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.ERR != null) {
			fixture.io.master.STB #= true
			mustFollowTheValueOfTheSelectedSlavesLine(_.ERR)
		}
	}

	"Multiplexed RTY" must "be false for invalid slave indices" in simulator { implicit fixture =>
		if (fixture.io.master.RTY != null) {
			forInvalidSlaves(master => master.RTY.toBoolean must be(false))((fixture, slave) => slave.RTY #= true)
		}
	}

	it must "follow the value of the selected slave's RTY when STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.RTY != null) {
			fixture.io.master.STB #= false
			mustFollowTheValueOfTheSelectedSlavesLine(_.RTY)
		}
	}

	it must "follow the value of the selected slave's RTY when STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.RTY != null) {
			fixture.io.master.STB #= true
			mustFollowTheValueOfTheSelectedSlavesLine(_.RTY)
		}
	}

	"Multiplexed STALL" must "be false for invalid slave indices" in simulator { implicit fixture =>
		if (fixture.io.master.STALL != null) {
			forInvalidSlaves(master => master.STALL.toBoolean must be(false))((fixture, slave) => slave.STALL #= true)
		}
	}

	it must "follow the value of the selected slave's STALL when STB is false" in simulator { implicit fixture =>
		if (fixture.io.master.STALL != null) {
			fixture.io.master.STB #= false
			mustFollowTheValueOfTheSelectedSlavesLine(_.STALL)
		}
	}

	it must "follow the value of the selected slave's STALL when STB is true" in simulator { implicit fixture =>
		if (fixture.io.master.STALL != null) {
			fixture.io.master.STB #= true
			mustFollowTheValueOfTheSelectedSlavesLine(_.STALL)
		}
	}
}
