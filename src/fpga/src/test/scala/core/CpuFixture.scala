package net.restall.ice40riscvsoc.tests.core

import java.util.UUID
import scala.util.Random

import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.core.cpu.{CpuGiven, CpuStateMachineBuilder}
import net.restall.ice40riscvsoc.tests.simulation.{Sampling, SimulationFixtureBase, WithNextSampling}

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
		val x8: Byte = 8
		val x9: Byte = 9
		val x10: Byte = 10
		val x18: Byte = 18

		val zero = x0
		val s1 = x9
		val s2 = x18

		def jal(rd: Byte, offset: Int) = j(0x6f, rd, offset)

		private def j(opcode: Byte, rd: Byte, imm: Int) =
			(bit(imm, 20) << 31) |
			(bits(imm, 10 downto 1) << 21) |
			(bit(imm, 11) << 20) |
			(bits(imm, 19 downto 12) << 12) |
			((rd & 0x1f) << 7) |
			(opcode & 0x7f)

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

		def xor(rd: Byte, rs1: Byte, rs2: Byte) = r(0x33, rd, rs1, rs2, 4, 0)

		private def r(opcode: Byte, rd: Byte, rs1: Byte, rs2: Byte, funct3: Byte, funct7: Byte) =
			((funct7 & 0x7f) << 25) |
			((rs2 & 0x1f) << 20) |
			((rs1 & 0x1f) << 15) |
			((funct3 & 0x07) << 12) |
			((rd & 0x1f) << 7) |
			(opcode & 0x7f)

		def nop() = addi(x0, x0, 0)

		def addi(rd: Byte, rs: Byte, imm: Int) = i(0x13, rd, rs, imm, 0)

		def canBeReadButMustNotBeExecuted() = lw(x0, x0, -1)

		def compact(instructions: CompactOpcodes => (Int, Int)) = {
			val pair = instructions(new CompactOpcodes())
			(pair._1.toLong & 0x0000ffffl) | ((pair._2.toLong << 16) & 0xffff0000l)
		}

		def lui(rd: Byte, imm: Int) = u(0x37, rd, imm)

		private def u(opcode: Byte, rd: Byte, imm: Long) =
			(bits(imm, 19 downto 0) << 12) |
			((rd & 0x1f) << 7) |
			(opcode & 0x7f)
	}

	class CompactOpcodes {
		val x8: Byte = 8
		val x9: Byte = 9
		val x10: Byte = 10

		def lw(rd: Byte, rs1: Byte, imm: Byte) = cl(0x00, rd, rs1, imm, 2)

		private def cl(op: Byte, rd: Byte, rs1: Byte, imm: Byte, funct3: Byte) =
			((funct3 & 0x07) << 13) |
			(bits(imm, 5 downto 3).toInt << 10) |
			((rs1 & 0x07) << 7) |
			(bit(imm, 2).toInt << 6) |
			(bit(imm, 6).toInt << 5) |
			((rd & 0x07) << 2) |
			(op & 0x03)

		def j(imm: Int) = cj(0x01, imm, 5)

		private def cj(op: Byte, imm: Int, funct3: Byte) =
			((funct3 & 0x07) << 13) |
			(bit(imm, 11).toInt << 12) |
			(bit(imm, 4).toInt << 11) |
			(bits(imm, 9 downto 8).toInt << 9) |
			(bit(imm, 10).toInt << 8) |
			(bit(imm, 6).toInt << 7) |
			(bit(imm, 7).toInt << 6) |
			(bits(imm, 3 downto 1).toInt << 3) |
			(bit(imm, 5).toInt << 2) |
			(op & 0x03)

		def addw(rd: Byte, rs2: Byte) = ca(0x01, rd, rs2, 1, 0x27)

		private def ca(op: Byte, rs1: Byte, rs2: Byte, funct2: Byte, funct6: Byte) =
			((funct6 & 0x3f) << 10) |
			((rs1 & 0x07) << 7) |
			((funct2 & 0x03) << 5) |
			((rs2 & 0x07) << 2) |
			(op & 0x03)

		def nop() = ???
	}

	private def bit(value: Long, bit: Int) = if ((value & (1 << bit)) != 0) 1 else 0

	private def bits(value: Long, bits: Range) = (value >> bits.min) & maskFor((bits.max - bits.min) downto 0)

	private def maskFor(bits: Range) = bits.foldLeft(0) { (acc, bit) => acc | (1 << bit) }
}
