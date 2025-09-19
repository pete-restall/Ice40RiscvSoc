package net.restall.ice40riscvsoc.tests.core.cpu

import net.restall.ice40riscvsoc.tests.givenwhenthen._
import net.restall.ice40riscvsoc.tests.simulation._

class CpuThen(builder: CpuStateMachineBuilder) extends Then with And[CpuThen] {
	def dataMustEqual(expectedAddressesAndWords: (Long, Long)*): CpuThen =
		new CpuThen(builder.assertDataEqualTo(expectedAddressesAndWords.toMap))

	def asStateMachine: Sampling = builder.build()

	override def and: CpuThen = this
}
