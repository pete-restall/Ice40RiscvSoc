package uk.co.lophtware.msfreference.tests.core

import scala.io.Source
import scala.util.{Random, Using}

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.simulation._

class CpuSimulationTest extends AnyFlatSpec with LightweightSimulationFixture[CpuFixture] {

	protected override def dutFactory() = new CpuFixture()

	"Cpu" must "produce a description of the CPU when yamlOutFilename has been specified" in simulator { fixture =>
		val yaml = Using(Source.fromFile(fixture.yamlOutFilename)) { file => file.getLines().toList }
		yaml.isSuccess must be(true)
	}

	it must "be able to execute simple instructions" in simulator { fixture =>
		val tokenAddress = Random.between(0, 128)
		val tokenValue = Random.nextLong() & 0xffffffffl
		val opcodes = fixture.opcodes
		val test = fixture
			.given.data(tokenAddress.toLong -> tokenValue)
			.and.instructions(fixture.adjustedAddressesFor(
				0l -> opcodes.lw(opcodes.x2, opcodes.x0, tokenAddress * 4),
				1l -> opcodes.xori(opcodes.x2, opcodes.x2, -1),
				2l -> opcodes.sw(opcodes.x0, opcodes.x2, tokenAddress * 4),
				3l -> opcodes.canBeReadButMustNotBeExecuted()))
			.when.dataWrittenTo(tokenAddress)
			.then.dataMustEqual(tokenAddress.toLong -> (~tokenValue & 0xffffffffl))

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	it must "be able to execute instructions with jumps" in simulator { fixture =>
		val tokenAddress = Random.between(0, 128)
		val tokenValue = Random.nextLong() & 0xffffffffl
		val opcodes = fixture.opcodes
		val test = fixture
			.given.data(tokenAddress.toLong -> tokenValue)
			.and.instructions(fixture.adjustedAddressesFor(
				0l -> opcodes.jal(opcodes.x0, 10 * 4),
				1l -> opcodes.canBeReadButMustNotBeExecuted(),

				10l -> opcodes.lw(opcodes.x2, opcodes.x0, tokenAddress * 4),
				11l -> opcodes.xori(opcodes.x2, opcodes.x2, -1),
				12l -> opcodes.sw(opcodes.x0, opcodes.x2, tokenAddress * 4),
				13l -> opcodes.canBeReadButMustNotBeExecuted()))
			.when.dataWrittenTo(tokenAddress)
			.then.dataMustEqual(tokenAddress.toLong -> (~tokenValue & 0xffffffffl))

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	// TODO: must be able to execute compact instructions

	// TODO: must be able to respond to interrupts
}
