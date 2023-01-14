package uk.co.lophtware.msfreference.tests.multiplexing

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.multiplexing.Encoder
import uk.co.lophtware.msfreference.tests.simulation._

class EncoderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	private class IoBundle(numberOfInputs: Int) extends Encoder.IoBundle(numberOfInputs) { }

	private val lessThanOneNumberOfInputs = tableFor("numberOfInputs", List(0, -1, -2, -11, -1024))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	"Encoder.IoBundle" must "not accept less than 1 input" in spinalContext { () =>
		forAll(lessThanOneNumberOfInputs) { (numberOfInputs: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new IoBundle(numberOfInputs))
			thrown.getMessage must include("arg=numberOfInputs")
		}}
	}

	private val numberOfInputs = tableFor("numberOfInputs", List(1, 2, 3, 4, anyNumberOfInputs()))

	private def anyNumberOfInputs() = Random.between(1, 64)

	it must "have IO for the number of inputs passed to the constructor" in spinalContext { () =>
		forAll(numberOfInputs) { (numberOfInputs: Int) => {
			val bundle = new IoBundle(numberOfInputs)
			bundle.inputs.length must be(numberOfInputs)
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
			val bundle = new IoBundle(numberOfInputs)
			bundle.output.getWidth must be(outputWidth)
		}}
	}
}
