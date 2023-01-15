package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.lib.{master, slave}
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

object WishboneTestDoubles {
	def stubMasterWith(config: WishboneConfig) = master(new Wishbone(config))

	def stubSlaveWith(config: WishboneConfig) = slave(new Wishbone(config))
}
