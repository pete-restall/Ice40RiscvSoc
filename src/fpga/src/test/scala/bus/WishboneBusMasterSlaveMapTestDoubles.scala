package uk.co.lophtware.msfreference.tests.bus

import spinal.lib.IMasterSlave
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.bus.MasterSlaveMap
import uk.co.lophtware.msfreference.tests.bus.wishbone.{WishboneTestDoubles, WishboneConfigTestDoubles}

object WishboneBusMasterSlaveMapTestDoubles {
	def dummy(): MasterSlaveMap[Wishbone] = new MasterSlaveMap[Wishbone](Seq(dummyMaster()), Seq(dummySlave()))

	private val dummyConfig = WishboneConfigTestDoubles.dummy()

	private def dummyMaster() = WishboneTestDoubles.stubMasterWith(dummyConfig)

	private def dummySlave() = WishboneTestDoubles.stubSlaveWith(dummyConfig)
}
