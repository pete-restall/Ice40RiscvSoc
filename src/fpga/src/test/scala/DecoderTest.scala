package uk.co.lophtware.msfreference.tests

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.Decoder
import uk.co.lophtware.msfreference.tests.simulation._

class DecoderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	"Decoder" must "not accept a null inputWidth" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(new Decoder(null))
		thrown.getMessage must (include("arg=inputWidth") and include("null"))
	}

	private val lessThanOneInputWidth = tableFor("inputWidth", List(0 bits, -1 bits, -2 bits, -11 bits, -1024 bits))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "not accept an input width less than 1 bit" in spinalContext { () =>
		forAll(lessThanOneInputWidth) { (inputWidth: BitCount) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Decoder(inputWidth))
			thrown.getMessage must include("arg=inputWidth")
		}}
	}

	private val sampleOfInputWidths = tableFor("inputWidth", List(1 bit, 2 bits, 3 bits, 4 bits, anyInputWidth()))

	private def anyInputWidth() = Random.between(1, 8) bits

	it must "have an input width the same as passed to the constructor" in spinalContext { () =>
		forAll(sampleOfInputWidths) { (inputWidth: BitCount) => {
			val encoder = new Decoder(inputWidth)
			encoder.io.input.getWidth must be(inputWidth.value)
		}}
	}

	it must "have an output for each possible combination of input (2^N for an N-bit input)" in spinalContext { () =>
		forAll(sampleOfInputWidths) { (inputWidth: BitCount) => {
			val encoder = new Decoder(inputWidth)
			encoder.io.outputs.length must be(1 << inputWidth.value)
		}}
	}

	"Decoder companion's apply() method" must "not accept a null input" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy Decoder(null)
		thrown.getMessage must (include("arg=input") and include("null"))
	}

	it must "return a Decoder with the same width input as passed to the constructor" in spinalContext { () =>
		forAll(sampleOfInputWidths) { (inputWidth: BitCount) =>
			val input = UInt(inputWidth)
			val decoder = Decoder(input)
			decoder.io.input.getWidth must be(inputWidth.value)
		}
	}
}
