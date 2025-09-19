package net.restall.ice40riscvsoc.tests.bus

import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MultiMasterSingleSlaveArbiter}

class CrossbarArbiterFixture(numberOfMasters: Int, numberOfSlaves: Int) extends Component with CrossbarArbiterFixtureTraits {
	override val io = new CrossbarArbiter.IoBundle(numberOfMasters, numberOfSlaves)
	private val dut = new CrossbarArbiter(numberOfMasters, numberOfSlaves)
	io <> dut.io

	override def setMasterRequest(slave: MultiMasterSingleSlaveArbiter.IoBundle, masterIndex: Int, value: Boolean): Unit = {
		slave.masters(masterIndex).request #= value
	}
}
