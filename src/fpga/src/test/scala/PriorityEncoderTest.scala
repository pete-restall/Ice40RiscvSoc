package uk.co.lophtware.msfreference.tests

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.PriorityEncoder
import uk.co.lophtware.msfreference.tests.simulation._

class PriorityEncoderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	private val lessThanOneNumberOfInputs = tableFor("numberOfInputs", List(0, -1, -2, -11, -1024))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	"PriorityEncoder" must "not accept less than 1 input" in spinalContext { () =>
		forAll(lessThanOneNumberOfInputs) { (numberOfInputs: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new PriorityEncoder(numberOfInputs))
			thrown.getMessage must include("arg=numberOfInputs")
		}}
	}

	private val numberOfInputs = tableFor("numberOfInputs", List(1, 2, 3, 4, anyNumberOfInputs()))

	private def anyNumberOfInputs() = Random.between(1, 64)

	it must "have IO for the number of inputs passed to the constructor" in spinalContext { () =>
		forAll(numberOfInputs) { (numberOfInputs: Int) => {
			val encoder = new PriorityEncoder(numberOfInputs)
			encoder.io.inputs.length must be(numberOfInputs)
		}}
	}

	private val outputWidthsVsNumberOfInputs = tableFor(("numberOfInputs", "outputWidth"), List(
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
		(257, 9)))

	private def tableFor[A, B](headers: (String, String), values: Iterable[(A, B)]) = Table(headers) ++ values

	it must "have an output width sufficient to cover all inputs" in spinalContext { () =>
		forAll(outputWidthsVsNumberOfInputs) { (numberOfInputs: Int, outputWidth: Int) => {
			val encoder = new PriorityEncoder(numberOfInputs)
			encoder.io.output.getWidth must be(outputWidth)
		}}
	}

	"PriorityEncoder companion's apply() method" must "not accept a null highestPriorityInput" in spinalContext { () =>
		val thrown = the [IllegalArgumentException] thrownBy(PriorityEncoder(null))
		thrown.getMessage must (include("arg=highestPriorityInput") and include("null"))
	}

	it must "not accept any null inputs" in spinalContext { () =>
		val inputsContainingNull = Random.shuffle(anyOtherInputs() :+ null)
		val thrown = the [IllegalArgumentException] thrownBy(PriorityEncoder(anyInput(), inputsContainingNull:_*))
		thrown.getMessage must (include("arg=otherInputs") and include("null"))
	}

	private def anyOtherInputs() = List.fill(anyNumberOfInputs() - 1) { anyInput() }

	private def anyInput() = if (Random.nextBoolean()) True else False

	it must "return a PriorityEncoder with the same number of IO as inputs" in spinalContext { () =>
		forAll(numberOfInputs) { (numberOfInputs: Int) =>
			val otherInputs = List.fill(numberOfInputs - 1) { anyInput() }
			val encoder = PriorityEncoder(anyInput(), otherInputs:_*)
			encoder.io.inputs.length must be(numberOfInputs)
		}
	}
}
