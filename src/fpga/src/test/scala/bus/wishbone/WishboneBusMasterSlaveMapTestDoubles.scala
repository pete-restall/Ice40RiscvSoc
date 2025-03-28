package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.core.False
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.bus.MasterSlaveMap
import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterSlaveMap

object WishboneBusMasterSlaveMapTestDoubles {
	def dummy(): MasterSlaveMap[Wishbone] = {
		val config = WishboneConfigTestDoubles.dummy()
		val master = WishboneTestDoubles.stubMasterWith(config)
		val slave = WishboneTestDoubles.stubSlaveWith(config)
		WishboneBusMasterSlaveMap((master, (bus: Wishbone) => False, slave))
	}

	def stubWith(numberOfMasters: Int=1, numberOfSlaves: Int=1): MasterSlaveMap[Wishbone] = {
		val config = WishboneConfigTestDoubles.dummy()
		val masters = Seq.fill(numberOfMasters) { WishboneTestDoubles.stubMasterWith(config) }
		val slaves = Seq.fill(numberOfSlaves) { WishboneTestDoubles.stubSlaveWith(config) }
		val cross = for (master <- masters; slave <- slaves) yield (master, (bus: Wishbone) => bus.CYC, slave)
		WishboneBusMasterSlaveMap(cross.head, cross.tail:_*)
	}
}
