package uk.co.lophtware.msfreference.tests.core

import java.util.UUID
import scala.util.Random

import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.core.cpu.{CpuGiven, CpuStateMachineBuilder}
import uk.co.lophtware.msfreference.tests.simulation.{Sampling, SimulationFixtureBase, WithNextSampling}

class CpuFixture() extends Component {
	val yamlOutFilename = "/tmp/" + UUID.randomUUID().toString

	val io = new Cpu.IoBundle()

	private val resetVector = anyResetVector()

	private def anyResetVector() = Random.between(0, 1024) & ~3

	private val dut = new Cpu(resetVector, anyMtvecInit(), Some(yamlOutFilename))
	io <> dut.io

	private def anyMtvecInit() = Random.between(0, 1024)

	def given = new CpuGiven(new CpuStateMachineBuilder(
		io,
		data=scala.collection.mutable.Map[Long, Long](),
		ibusFactoryStack=List[Sampling => WithNextSampling](),
		dbusFactoryStack=List[Sampling => WithNextSampling](),
		assertionFactoryStack=List[Sampling => WithNextSampling]()))

	def wireStimuliUsing(initialState: Sampling) = {
		initialState.mustNotBeNull("initialState")

		var state = initialState
		clockDomain.assertReset()
		clockDomain.onSamplings { state = state.onSampling() }
		clockDomain.forkStimulus(period=10)
		clockDomain.waitEdge(3)
		clockDomain.deassertReset()
		SimulationFixtureBase.waitForExplicitSimulationTermination
	}

	def adjustedAddressesFor(instructions: (Long, Long)*) = instructions.map {
		case(address, instruction) => ((resetVector >> 2) + address, instruction)
	}.toMap

	val opcodes = new CpuFixture.Opcodes()
}

object CpuFixture {
	class Opcodes {
		val x0: Byte = 0
		val x1: Byte = 1
		val x2: Byte = 2

		def jal(rd: Byte, offset: Int) = j(0x6f, rd, offset)

		private def j(opcode: Byte, rd: Byte, imm: Int) =
			(bit(imm, 20) << 31) |
			(bits(imm, 10 downto 1) << 21) |
			(bit(imm, 11) << 20) |
			(bits(imm, 19 downto 12) << 12) |
			((rd & 0x1f) << 7) |
			(opcode & 0x7f)

		private def bit(value: Long, bit: Int) = if ((value & (1 << bit)) != 0) 1 else 0

		private def bits(value: Long, bits: Range) = (value >> bits.min) & maskFor((bits.max - bits.min) downto 0)

		private def maskFor(bits: Range) = bits.foldLeft(0) { (acc, bit) => acc | (1 << bit) }

		def lw(rd: Byte, rs1: Byte, offset: Int) = i(0x03, rd, rs1, offset, 2)

		private def i(opcode: Byte, rd: Byte, rs1: Byte, imm: Int, funct3: Byte) =
			(bits(imm, 11 downto 0) << 20) |
			((rs1 & 0x1f) << 15) |
			((funct3 & 0x07) << 12) |
			((rd & 0x1f) << 7) |
			(opcode & 0x7f)

		def sw(rs1: Byte, rs2: Byte, offset: Int) = s(0x23, rs1, rs2, offset, 2)

		private def s(opcode: Byte, rs1: Byte, rs2: Byte, imm: Int, funct3: Byte) =
			(bits(imm, 11 downto 5) << 25) |
			((rs2 & 0x1f) << 20) |
			((rs1 & 0x1f) << 15) |
			((funct3 & 0x07) << 12) |
			(bits(imm, 4 downto 0) << 7) |
			(opcode & 0x7f)

		def xori(rd: Byte, rs: Byte, imm: Int) = i(0x13, rd, rs, imm, 4)

		def nop() = addi(x0, x0, 0)

		def addi(rd: Byte, rs: Byte, imm: Int) = i(0x13, rd, rs, imm, 0)

		def canBeReadButMustNotBeExecuted() = lw(x0, x0, -1)
	}
}
