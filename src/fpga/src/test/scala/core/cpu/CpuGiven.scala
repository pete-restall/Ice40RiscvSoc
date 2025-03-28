package uk.co.lophtware.msfreference.tests.core.cpu

import uk.co.lophtware.msfreference.tests.givenwhenthen._

class CpuGiven(builder: CpuStateMachineBuilder) extends GivenAnd[CpuGiven, CpuWhen] {
	def instructions(instructions: (Long, Long)*): GivenAnd[CpuGiven, CpuWhen] = this.instructions(instructions.toMap)

	def instructions(instructions: Map[Long, Long])(implicit dummy: DummyImplicit): GivenAnd[CpuGiven, CpuWhen] = new CpuGiven(builder.withInstructions(instructions))

	def data(data: (Long, Long)*): GivenAnd[CpuGiven, CpuWhen] = this.data(data.toMap)

	def data(data: Map[Long, Long])(implicit dummy: DummyImplicit): GivenAnd[CpuGiven, CpuWhen] = new CpuGiven(builder.withData(data))

	override def when: CpuWhen = new CpuWhen(builder)

	override def and: CpuGiven = this
}
