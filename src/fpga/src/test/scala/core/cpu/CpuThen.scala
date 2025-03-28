package uk.co.lophtware.msfreference.tests.core.cpu

import uk.co.lophtware.msfreference.tests.givenwhenthen._
import uk.co.lophtware.msfreference.tests.simulation._

class CpuThen(builder: CpuStateMachineBuilder) extends Then with And[CpuThen] {
	def dataMustEqual(expectedAddressesAndWords: (Long, Long)*): CpuThen =
		new CpuThen(builder.assertDataEqualTo(expectedAddressesAndWords.toMap))

	def asStateMachine: Sampling = builder.build()

	override def and: CpuThen = this
}
