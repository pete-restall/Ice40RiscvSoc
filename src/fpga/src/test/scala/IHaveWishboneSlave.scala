package uk.co.lophtware.msfreference.tests

import spinal.lib.bus.wishbone.Wishbone

trait IHaveWishboneSlave {
	def asWishboneSlave: Wishbone
}
