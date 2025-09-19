package net.restall.ice40riscvsoc.tests.bus

import net.restall.ice40riscvsoc.bus.CrossbarArbiter

object CrossbarArbiterTestDoubles {
	def dummy(): CrossbarArbiter = new CrossbarArbiter(numberOfMasters=1, numberOfSlaves=1)
}
