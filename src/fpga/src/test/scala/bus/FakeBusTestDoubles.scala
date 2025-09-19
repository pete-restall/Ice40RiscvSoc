package net.restall.ice40riscvsoc.tests.bus

import spinal.lib.{master, slave}

object FakeBusTestDoubles {
	def stubMaster(): FakeBus = master(new FakeBus())

	def stubSlave(): FakeBus = slave(new FakeBus())
}
