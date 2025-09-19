package net.restall.ice40riscvsoc.tests.bus.wishbone

import spinal.lib.bus.wishbone.Wishbone

trait IHaveWishboneSlave {
	def asWishboneSlave: Wishbone
}
