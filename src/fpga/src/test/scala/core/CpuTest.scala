package uk.co.lophtware.msfreference.tests.core

import java.util.UUID
import scala.io.Source
import scala.util.{Random, Using}

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._

import uk.co.lophtware.msfreference.core.Cpu
import uk.co.lophtware.msfreference.tests.simulation._

class CpuTest extends AnyFlatSpec with NonSimulationFixture {
	"Cpu" must "not use the 'io' prefix for signals" in spinalContext { () =>
		val cpu = new Cpu(anyResetVector(), anyMtvecInit(), dummyYamlOutFilename())
		cpu.io.name must be("")
	}

	private def anyResetVector() = Random.nextInt()

	private def anyMtvecInit() = Random.nextInt()

	private def dummyYamlOutFilename() = None

	it must "not accept a null yamlOutFilename" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Cpu(anyResetVector(), anyMtvecInit(), null))
		thrown.getMessage must (include("arg=yamlOutFilename") and include("null"))
	}
}
