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

	/* TODO: THIS DOES NOT CURRENTLY WORK.

	I suspect it's due to the combination of enabled / disabled / switched vexriscv features, or possibly that the Wishbone adapter is hard-coded to only
	address 32-bit words rather than 16-bit half-words.  See https://github.com/SpinalHDL/VexRiscv/issues/93#issuecomment-542762269 for some possible clues.
	Currently parked because RVC support is not a necessity at the moment.

	it must "be able to execute compact instructions" in simulator { fixture =>
		val (tokenAddress1, tokenValue1) = (Random.between(0, 32).toByte, Random.nextLong() & 0xffffffffl)
		val (tokenAddress2, tokenValue2) = (Random.between(0, 32).toByte, Random.nextLong() & 0xffffffffl)
		val opcodes = fixture.opcodes
		val test = fixture
			.given.data(
				tokenAddress1.toLong -> tokenValue1,
				tokenAddress2.toLong -> tokenValue2)
			.and.instructions(fixture.adjustedAddressesFor(
				0l -> opcodes.xor(opcodes.x10, opcodes.x10, opcodes.x10),
				1l -> opcodes.compact(c => (
					c.lw(c.x8, c.x10, tokenAddress1),
					c.j(63 * 4))
				),
				2l -> opcodes.canBeReadButMustNotBeExecuted(),

				64l -> opcodes.compact(c => (
					c.lw(c.x9, c.x10, tokenAddress2),
					c.addw(c.x9, c.x8))
				),
				65l -> opcodes.sw(opcodes.x0, opcodes.x9, tokenAddress1 * 4),
				66l -> opcodes.canBeReadButMustNotBeExecuted()))
			.when.dataWrittenTo(tokenAddress1)
			.then.dataMustEqual(
				tokenAddress1.toLong -> ((tokenValue1 + tokenValue2) & 0xffffffffl),
				tokenAddress2.toLong -> tokenValue2)

		fixture.wireStimuliUsing(test.asStateMachine)
	}
	*/

	it must "be able to execute and store the results of 32-bit li instructions" in simulator { fixture =>
		val address = 50
		val offset = 1
		val offsetAddress = (address + offset).toLong
		val opcodes = fixture.opcodes
		val test = fixture
			.given.data((offsetAddress -> 0l))
			.and.instructions(fixture.adjustedAddressesFor(
				0l -> opcodes.addi(opcodes.s2, opcodes.zero, address * 4),
				1l -> opcodes.lui(opcodes.s1, 0x14a),
				2l -> opcodes.addi(opcodes.s1, opcodes.s1, -1773),
				3l -> opcodes.sw(opcodes.s2, opcodes.s1, offset * 4),
				4l -> opcodes.canBeReadButMustNotBeExecuted(),
				5l -> opcodes.canBeReadButMustNotBeExecuted()))
			.when.dataWrittenTo(offsetAddress)
			.then.dataMustEqual(offsetAddress -> 0x00149913l)

		fixture.wireStimuliUsing(test.asStateMachine)
	}

	// TODO: must be able to respond to interrupts
}
