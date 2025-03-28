package uk.co.lophtware.msfreference.tests.bus

import uk.co.lophtware.msfreference.bus.CrossbarArbiter

object CrossbarArbiterTestDoubles {
	def dummy(): CrossbarArbiter = new CrossbarArbiter(numberOfMasters=1, numberOfSlaves=1)
}
