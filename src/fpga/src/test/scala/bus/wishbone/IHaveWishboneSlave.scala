package uk.co.lophtware.msfreference.tests.bus.wishbone

import spinal.lib.bus.wishbone.Wishbone

trait IHaveWishboneSlave {
	def asWishboneSlave: Wishbone
}
