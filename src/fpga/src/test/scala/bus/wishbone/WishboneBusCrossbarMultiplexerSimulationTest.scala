package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.AppendedClues._
import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.tests.simulation._

class WishboneBusCrossbarMultiplexerSimulationTest(busConfig: WishboneConfig, numberOfMasters: Int, numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[WishboneBusCrossbarMultiplexerFixture]
	with Inspectors {

	protected override def dutFactory() = new WishboneBusCrossbarMultiplexerFixture(busConfig, numberOfMasters, numberOfSlaves, dutCreatedViaApplyFactory)

	"WishboneBusCrossbarMultiplexer" must "route all MOSI signals from each master to its granted slave" in simulator { fixture =>
		forAll(fixture.allMasterAndSlaveCombinations.zipWithIndex) { case ((master, slave), index) =>
			fixture.io.selector #= index
			sleep(1)
			mosiSignalsMustPropagate(master, slave)
		}
	}

	private def mosiSignalsMustPropagate(master: Wishbone, slave: Wishbone): Unit = {
		signalMustPropagate("CYC", master.CYC, slave.CYC)
		signalMustPropagate("STB", master.STB, slave.STB)
		signalMustPropagate("WE", master.WE, slave.WE)
		signalsMustPropagate("ADR", master.ADR, slave.ADR)
		signalsMustPropagate("DAT_MOSI", master.DAT_MOSI, slave.DAT_MOSI)
		Option(master.LOCK).map(signalMustPropagate("LOCK", _, slave.LOCK))
		Option(master.SEL).map(signalsMustPropagate("SEL", _, slave.SEL))
		Option(master.BTE).map(signalsMustPropagate("BTE", _, slave.BTE))
		Option(master.CTI).map(signalsMustPropagate("CTI", _, slave.CTI))
		Option(master.TGA).map(signalsMustPropagate("TGA", _, slave.TGA))
		Option(master.TGC).map(signalsMustPropagate("TGC", _, slave.TGC))
		Option(master.TGD_MOSI).map(signalsMustPropagate("TGD_MOSI", _, slave.TGD_MOSI))
	}

	private def signalMustPropagate(name: String, masterSignal: Bool, slaveSignal: Bool): Unit = {
		Seq(true, false, true).foreach { value =>
			masterSignal #= value
			sleep(1)
			slaveSignal.toBoolean must be(value) withClue s"for signal ${name}"
		}
	}

	private def signalsMustPropagate(name: String, masterSignal: BitVector, slaveSignal: BitVector): Unit = {
		for (bitNumber <- 0 until masterSignal.getWidth) {
			val value = 1l << bitNumber
			masterSignal #= value
			sleep(1)
			slaveSignal.toLong must be(value) withClue s"for signal ${name}"
		}
	}

	it must "route all MISO signals from each slave to the master that has requesting it" in simulator { fixture =>
		forAll(fixture.allMasterAndSlaveCombinations.zipWithIndex) { case ((master, slave), index) =>
			fixture.io.selector #= index
			sleep(1)
			misoSignalsMustPropagate(master, slave)
		}
	}

	private def misoSignalsMustPropagate(master: Wishbone, slave: Wishbone): Unit = {
		signalMustPropagate("ACK", slave.ACK, master.ACK)
		Option(slave.ERR).map(signalMustPropagate("ERR", _, master.ERR))
		Option(slave.RTY).map(signalMustPropagate("RTY", _, master.RTY))
		Option(slave.STALL).map(signalMustPropagate("STALL", _, master.STALL))
		Option(slave.DAT_MISO).map(signalsMustPropagate("DAT_MISO", _, master.DAT_MISO))
		Option(slave.TGD_MISO).map(signalsMustPropagate("TGD_MISO", _, master.TGD_MISO))
	}

	// FURTHER TESTS THAT NEED TO BE WRITTEN TO VERIFY THE BEHAVIOUR:
	// TODO: WHAT HAPPENS WHEN REQUESTED SLAVE IS NOT EQUAL TO GRANTED SLAVE ?
	// TODO: WHAT HAPPENS WHEN SELECTED MASTER DOES NOT EXIST ?
	// TODO: WHAT HAPPENS WHEN SELECTED SLAVE DOES NOT EXIST ?
}
