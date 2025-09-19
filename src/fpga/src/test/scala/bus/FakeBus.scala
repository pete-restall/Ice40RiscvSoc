package net.restall.ice40riscvsoc.tests.bus

import spinal.core._
import spinal.lib.IMasterSlave

class FakeBus extends Bundle with IMasterSlave {
	val address = in UInt(16 bits)
	val data = in UInt(16 bits)

	override def asMaster(): Unit = {
		address.asOutput()
		data.asOutput()
	}

	override def asSlave(): Unit = {
		address.asInput()
		data.asInput()
	}
}
