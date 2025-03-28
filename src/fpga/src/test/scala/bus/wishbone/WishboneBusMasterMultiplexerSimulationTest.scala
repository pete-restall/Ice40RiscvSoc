package uk.co.lophtware.msfreference.tests.bus.wishbone

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterMultiplexer
import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusMasterMultiplexerSimulationTest(busConfig: WishboneConfig, numberOfMasters: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusMasterMultiplexerFixture]
	with Inspectors {

	protected override def dutFactory() = new WishboneBusMasterMultiplexerFixture(busConfig, numberOfMasters, dutCreatedViaApplyFactory)

	"WishboneBusMasterMultiplexer masters" must "all have a non-multiplexed slave DAT_MISO" in simulator { fixture =>
		fixture.io.slave.DAT_MISO #= fixture.anyData()
		fixture.io.selector #= fixture.anyMasterIndex()
		sleep(1)
		forAll(fixture.io.masters) { master => master.DAT_MISO.toLong must be(fixture.io.slave.DAT_MISO.toLong) }
	}

	they must "all have a non-multiplexed slave TGD_MISO" in simulator { fixture =>
		if (fixture.io.slave.TGD_MISO != null) {
			fixture.io.slave.TGD_MISO #= fixture.anyTagData()
			fixture.io.selector #= fixture.anyMasterIndex()
			sleep(1)
			forAll(fixture.io.masters) { master => master.TGD_MISO.toLong must be(fixture.io.slave.TGD_MISO.toLong) }
		}
	}

	they must "all have their DAT_MOSI multiplexed to the slave" in simulator { fixture =>
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		fixture.io.masters.foreach(master => master.DAT_MOSI #= fixture.anyData())
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= index
			sleep(1)
			fixture.io.slave.DAT_MOSI.toLong must be(master.DAT_MOSI.toLong)
		}
	}

	they must "all have their ADR multiplexed to the slave" in simulator { fixture =>
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		fixture.io.masters.foreach(master => master.ADR #= fixture.anyAddress())
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= index
			sleep(1)
			fixture.io.slave.ADR.toLong must be(master.ADR.toLong)
		}
	}

	they must "all have their SEL multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.SEL != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.SEL #= fixture.anySel())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.SEL.toInt must be(master.SEL.toInt)
			}
		}
	}

	they must "all have their BTE multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.BTE != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.BTE #= fixture.anyBte())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.BTE.toInt must be(master.BTE.toInt)
			}
		}
	}

	they must "all have their CTI multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.CTI != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.CTI #= fixture.anyCti())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.CTI.toInt must be(master.CTI.toInt)
			}
		}
	}

	they must "all have their TGA multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.TGA != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.TGA #= fixture.anyTga())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.TGA.toInt must be(master.TGA.toInt)
			}
		}
	}

	they must "all have their TGC multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.TGC != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.TGC #= fixture.anyTgc())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.TGC.toInt must be(master.TGC.toInt)
			}
		}
	}

	they must "all have their TGD_MOSI multiplexed to the slave" in simulator { fixture =>
		if (fixture.io.slave.TGD_MOSI != null) {
			val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
			fixture.io.masters.foreach(master => master.TGD_MOSI #= fixture.anyTagData())
			forAll(mastersInAnyOrder) { case(master, index) =>
				fixture.io.selector #= index
				sleep(1)
				fixture.io.slave.TGD_MOSI.toInt must be(master.TGD_MOSI.toInt)
			}
		}
	}

	they must "only have true ACK when selected and the slave ACK is also true" in simulator { implicit fixture =>
		mustOnlyHaveTrueLineWhenSelectedAndTheSlaveLineIsAlsoTrue(_.ACK)
	}

	private def mustOnlyHaveTrueLineWhenSelectedAndTheSlaveLineIsAlsoTrue(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) = {
		ioLineFrom(fixture.io.slave) #= true
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= index
			sleep(1)
			ioLineFrom(master).toBoolean must be(true)
		}
	}

	they must "have false ACK when not selected and the slave ACK is also true" in simulator { implicit fixture =>
		mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoTrue(_.ACK)
	}

	private def mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoTrue(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) = {
		ioLineFrom(fixture.io.slave) #= true
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= fixture.anyMasterIndexExcept(index)
			sleep(1)
			ioLineFrom(master).toBoolean must be(false)
		}
	}

	they must "have false ACK when selected and the slave ACK is also false" in simulator { implicit fixture =>
		mustHaveFalseLineWhenSelectedAndTheSlaveLineIsAlsoFalse(_.ACK)
	}

	private def mustHaveFalseLineWhenSelectedAndTheSlaveLineIsAlsoFalse(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) = {
		ioLineFrom(fixture.io.slave) #= false
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= index
			sleep(1)
			ioLineFrom(master).toBoolean must be(false)
		}
	}

	they must "have false ACK when not selected and the slave ACK is also false" in simulator { implicit fixture =>
		mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoFalse(_.ACK)
	}

	private def mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoFalse(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) = {
		ioLineFrom(fixture.io.slave) #= false
		val mastersInAnyOrder = Random.shuffle(fixture.io.masters.zipWithIndex.toSeq)
		forAll(mastersInAnyOrder) { case(master, index) =>
			fixture.io.selector #= fixture.anyMasterIndexExcept(index)
			sleep(1)
			ioLineFrom(master).toBoolean must be(false)
		}
	}

	they must "only have true ERR when selected and the slave ERR is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.ERR != null) {
			mustOnlyHaveTrueLineWhenSelectedAndTheSlaveLineIsAlsoTrue(_.ERR)
		}
	}

	they must "have false ERR when not selected and the slave RTY is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.ERR != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoTrue(_.ERR)
		}
	}

	they must "have false ERR when selected and the slave ERR is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.ERR != null) {
			mustHaveFalseLineWhenSelectedAndTheSlaveLineIsAlsoFalse(_.ERR)
		}
	}

	they must "have false ERR when not selected and the slave ERR is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.ERR != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoFalse(_.ERR)
		}
	}

	they must "only have true RTY when selected and the slave RTY is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.RTY != null) {
			mustOnlyHaveTrueLineWhenSelectedAndTheSlaveLineIsAlsoTrue(_.RTY)
		}
	}

	they must "have false RTY when not selected and the slave RTY is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.RTY != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoTrue(_.RTY)
		}
	}

	they must "have false RTY when selected and the slave RTY is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.RTY != null) {
			mustHaveFalseLineWhenSelectedAndTheSlaveLineIsAlsoFalse(_.RTY)
		}
	}

	they must "have false RTY when not selected and the slave RTY is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.RTY != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoFalse(_.RTY)
		}
	}

	they must "only have true STALL when selected and the slave STALL is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.STALL != null) {
			mustOnlyHaveTrueLineWhenSelectedAndTheSlaveLineIsAlsoTrue(_.STALL)
		}
	}

	they must "have false STALL when not selected and the slave STALL is also true" in simulator { implicit fixture =>
		if (fixture.io.slave.STALL != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoTrue(_.STALL)
		}
	}

	they must "have false STALL when selected and the slave STALL is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.STALL != null) {
			mustHaveFalseLineWhenSelectedAndTheSlaveLineIsAlsoFalse(_.STALL)
		}
	}

	they must "have false STALL when not selected and the slave STALL is also false" in simulator { implicit fixture =>
		if (fixture.io.slave.STALL != null) {
			mustHaveFalseLineWhenNotSelectedAndTheSlaveLineIsAlsoFalse(_.STALL)
		}
	}

	"Slave" must "have zero DAT_MOSI for invalid master indices" in simulator { implicit fixture =>
		forInvalidMasters(slave => slave.DAT_MOSI.toLong must be(0))((fixture, master) => master.DAT_MOSI #= fixture.anyData())
	}

	private def forInvalidMasters(expectation: Wishbone => Unit)(arrange: (WishboneBusMasterMultiplexerFixture, Wishbone) => Unit)(implicit fixture: WishboneBusMasterMultiplexerFixture) {
		val possibleNumberOfMasters = fixture.io.selector.maxValue.toLong + 1
		if (fixture.io.masters.length < possibleNumberOfMasters) {
			val invalidMastersInAnyOrder = Set(fixture.io.masters.length, possibleNumberOfMasters - 1) ++ Set.fill(10) { Random.between(fixture.io.masters.length, possibleNumberOfMasters) }
			fixture.io.masters.foreach(master => arrange(fixture, master))
			forAll(invalidMastersInAnyOrder) { index =>
				fixture.io.selector #= index
				sleep(1)
				expectation(fixture.io.slave)
			}
		}
	}

	it must "have zero ADR for invalid master indices" in simulator { implicit fixture =>
		forInvalidMasters(slave => slave.ADR.toInt must be(0)) { (fixture, master) =>
			master.ADR #= fixture.anyAddress()
		}
	}

	it must "have false WE for invalid master indices" in simulator { implicit fixture =>
		forInvalidMasters(slave => slave.WE.toBoolean must be(false)) { (fixture, master) =>
			master.WE #= true
		}
	}

	it must "have false CYC for invalid master indices" in simulator { implicit fixture =>
		forInvalidMasters(slave => slave.CYC.toBoolean must be(false)) { (fixture, master) =>
			master.CYC #= true
		}
	}

	it must "have false STB for invalid master indices" in simulator { implicit fixture =>
		forInvalidMasters(slave => slave.STB.toBoolean must be(false)) { (fixture, master) =>
			master.STB #= true
		}
	}

	it must "have false LOCK for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.LOCK != null) {
			forInvalidMasters(slave => slave.LOCK.toBoolean must be(false)) { (fixture, master) =>
				master.LOCK #= true
			}
		}
	}

	it must "have zero SEL for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.SEL != null) {
			forInvalidMasters(slave => slave.SEL.toInt must be(0)) { (fixture, master) =>
				master.SEL #= fixture.anySel()
			}
		}
	}

	it must "have zero BTE for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.BTE != null) {
			forInvalidMasters(slave => slave.BTE.toInt must be(0)) { (fixture, master) =>
				master.BTE #= fixture.anyBte()
			}
		}
	}

	it must "have zero CTI for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.CTI != null) {
			forInvalidMasters(slave => slave.CTI.toInt must be(0)) { (fixture, master) =>
				master.CTI #= fixture.anyCti()
			}
		}
	}

	it must "have zero TGA for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.TGA != null) {
			forInvalidMasters(slave => slave.TGA.toInt must be(0)) { (fixture, master) =>
				master.TGA #= fixture.anyTga()
			}
		}
	}

	it must "have zero TGC for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.TGC != null) {
			forInvalidMasters(slave => slave.TGC.toInt must be(0)) { (fixture, master) =>
				master.TGC #= fixture.anyTgc()
			}
		}
	}

	it must "have zero TGD_MOSI for invalid master indices" in simulator { implicit fixture =>
		if (fixture.io.slave.TGD_MOSI != null) {
			forInvalidMasters(slave => slave.TGD_MOSI.toInt must be(0)) { (fixture, master) =>
				master.TGD_MOSI #= fixture.anyTagData()
			}
		}
	}

	it must "follow the value of the selected master's WE" in simulator { implicit fixture =>
		mustFollowTheValueOfTheSelectedMastersLine(_.WE)
	}

	private def mustFollowTheValueOfTheSelectedMastersLine(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) {
		forAll(fixture.io.masters.zipWithIndex) { case(master, index) =>
			fixture.io.selector #= index
			ioLineFrom(master) #= false
			sleep(1)
			ioLineFrom(fixture.io.slave).toBoolean must be(false)

			ioLineFrom(master) #= true
			sleep(1)
			ioLineFrom(fixture.io.slave).toBoolean must be(true)
		}
	}

	it must "follow the value of the selected master's CYC" in simulator { implicit fixture =>
		mustFollowTheValueOfTheSelectedMastersLine(_.CYC)
	}

	it must "not follow the value of an unselected master's CYC" in simulator { implicit fixture =>
		mustNotFollowTheValueOfAnUnselectedMastersLine(_.CYC)
	}

	private def mustNotFollowTheValueOfAnUnselectedMastersLine(ioLineFrom: Wishbone => Bool)(implicit fixture: WishboneBusMasterMultiplexerFixture) {
		forAll(fixture.io.masters.zipWithIndex) { case(master, index) =>
			fixture.io.masters.foreach { otherMaster => ioLineFrom(otherMaster) #= otherMaster != master }
			fixture.io.selector #= index
			sleep(1)
			ioLineFrom(fixture.io.slave).toBoolean must be(false)

			fixture.io.masters.foreach { otherMaster => ioLineFrom(otherMaster) #= otherMaster == master }
			sleep(1)
			ioLineFrom(fixture.io.slave).toBoolean must be(true)
		}
	}

	it must "follow the value of the selected master's STB" in simulator { implicit fixture =>
		mustFollowTheValueOfTheSelectedMastersLine(_.STB)
	}

	it must "not follow the value of an unselected master's STB" in simulator { implicit fixture =>
		mustNotFollowTheValueOfAnUnselectedMastersLine(_.STB)
	}

	it must "follow the value of the selected master's LOCK" in simulator { implicit fixture =>
		if (fixture.io.slave.LOCK != null) {
			mustFollowTheValueOfTheSelectedMastersLine(_.LOCK)
		}
	}

	it must "not follow the value of an unselected master's LOCK" in simulator { implicit fixture =>
		if (fixture.io.slave.LOCK != null) {
			mustNotFollowTheValueOfAnUnselectedMastersLine(_.LOCK)
		}
	}
}
