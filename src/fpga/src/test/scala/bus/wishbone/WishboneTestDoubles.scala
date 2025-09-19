package net.restall.ice40riscvsoc.tests.bus.wishbone

import spinal.lib.{master, slave}
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

object WishboneTestDoubles {
	def dummy() = new Wishbone(WishboneConfigTestDoubles.dummy())

	def stubMasterWith(config: WishboneConfig) = master(new Wishbone(config))

	def stubSlaveWith(config: WishboneConfig) = slave(new Wishbone(config))
}
