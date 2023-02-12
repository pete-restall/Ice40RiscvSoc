package uk.co.lophtware.msfreference.tests.multiplexing

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.multiplexing.SimpleEncoder
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class SimpleEncoderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	"SimpleEncoder" must "not use the 'io' prefix for signals" in spinalContext {
		val encoder = new SimpleEncoder(numberOfInputs=1)
		encoder.io.name must be("")
	}

	private val lessThanOneNumberOfInputs = Seq(0, -1, -2, -11, -1024).asTable("numberOfInputs")

	it must "not accept less than 1 input" in spinalContext {
		forAll(lessThanOneNumberOfInputs) { (numberOfInputs: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new SimpleEncoder(numberOfInputs))
			thrown.getMessage must include("arg=numberOfInputs")
		}}
	}

	private val numberOfInputs = Seq(1, 2, 3, 4, anyNumberOfInputs()).asTable("numberOfInputs")

	private def anyNumberOfInputs() = Random.between(1, 64)

	it must "have IO for the number of inputs passed to the constructor" in spinalContext {
		forAll(numberOfInputs) { (numberOfInputs: Int) => {
			val encoder = new SimpleEncoder(numberOfInputs)
			encoder.io.inputs.length must be(numberOfInputs)
		}}
	}

	private val outputWidthsVsNumberOfInputs = Seq(
		(1, 1),
		(2, 1),
		(3, 2),
		(4, 2),
		(5, 3),
		(8, 3),
		(9, 4),
		(15, 4),
		(16, 4),
		(17, 5),
		(256, 8),
		(257, 9)
	).asTable("numberOfInputs", "outputWidth")

	it must "have an output width sufficient to cover all inputs" in spinalContext {
		forAll(outputWidthsVsNumberOfInputs) { (numberOfInputs: Int, outputWidth: Int) => {
			val encoder = new SimpleEncoder(numberOfInputs)
			encoder.io.output.getWidth must be(outputWidth)
		}}
	}

	"SimpleEncoder companion's apply() method" must "not accept a null firstInput" in spinalContext {
		val thrown = the [IllegalArgumentException] thrownBy SimpleEncoder(null)
		thrown.getMessage must (include("arg=firstInput") and include("null"))
	}

	it must "not accept any null inputs" in spinalContext {
		val inputsContainingNull = Random.shuffle(anyOtherInputs() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy SimpleEncoder(anyInput(), inputsContainingNull:_*)
		thrown.getMessage must (include("arg=otherInputs") and include("null"))
	}

	private def anyOtherInputs() = Seq.fill(anyNumberOfInputs() - 1) { anyInput() }

	private def anyInput() = if (Random.nextBoolean()) True else False

	it must "return a Encoder with the same number of IO as inputs" in spinalContext {
		forAll(numberOfInputs) { (numberOfInputs: Int) =>
			val otherInputs = List.fill(numberOfInputs - 1) { anyInput() }
			val encoder = SimpleEncoder(anyInput(), otherInputs:_*)
			encoder.io.inputs.length must be(numberOfInputs)
		}
	}
}
