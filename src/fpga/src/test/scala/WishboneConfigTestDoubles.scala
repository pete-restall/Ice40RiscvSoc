package uk.co.lophtware.msfreference.tests

import scala.util.Random

import spinal.lib.bus.wishbone.WishboneConfig

object WishboneConfigTestDoubles {
	def dummy() = stub()

	def stub() = stubWith()

	def stubWith(
		addressWidth: Int = Random.between(1, 64),
		dataWidth: Int = Random.between(1, 64),
		selWidth: Int = Random.between(0, 64),
		useSTALL: Boolean = Random.nextBoolean(),
		useLOCK: Boolean = Random.nextBoolean(),
		useERR: Boolean = Random.nextBoolean(),
		useRTY: Boolean = Random.nextBoolean(),
		useCTI: Boolean = Random.nextBoolean(),
		tgaWidth: Int = Random.between(0, 64),
		tgcWidth: Int = Random.between(0, 64),
		tgdWidth: Int = Random.between(0, 64),
		useBTE: Boolean = Random.nextBoolean()) = new WishboneConfig(
			addressWidth,
			dataWidth,
			selWidth,
			useSTALL,
			useLOCK,
			useERR,
			useRTY,
			useCTI,
			tgaWidth,
			tgcWidth,
			tgdWidth,
			useBTE)
}
