package net.restall.ice40riscvsoc.tests.core

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._

import net.restall.ice40riscvsoc.core.Cpu
import net.restall.ice40riscvsoc.tests.simulation._

class CpuTest extends AnyFlatSpec with NonSimulationFixture {
	"Cpu" must "not use the 'io' prefix for signals" in fullSpinalContext {
		val cpu = new Cpu(anyResetVector(), anyMtvecInit(), dummyYamlOutFilename())
		cpu.io.name must be("")
	}

	private def anyResetVector() = Random.between(0, 0x100000000l)

	private def anyMtvecInit() = Random.between(0, 0x100000000l)

	private def dummyYamlOutFilename() = None

	it must "not accept a null yamlOutFilename" in fullSpinalContext {
		val thrown = the [IllegalArgumentException] thrownBy(new Cpu(anyResetVector(), anyMtvecInit(), null))
		thrown.getMessage must (include("arg=yamlOutFilename") and include("null"))
	}
}
