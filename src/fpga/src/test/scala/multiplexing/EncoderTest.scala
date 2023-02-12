package uk.co.lophtware.msfreference.tests.multiplexing

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._

import uk.co.lophtware.msfreference.multiplexing.Encoder
import uk.co.lophtware.msfreference.tests.IterableTableExtensions._
import uk.co.lophtware.msfreference.tests.simulation._

class EncoderTest extends AnyFlatSpec with NonSimulationFixture with TableDrivenPropertyChecks {
	private val lessThanOneNumberOfInputs = Seq(0, -1, -2, -11, -1024).asTable("numberOfInputs")

	"Encoder.IoBundle" must "not accept less than 1 input" in spinalContext {
		forAll(lessThanOneNumberOfInputs) { (numberOfInputs: Int) => {
			val thrown = the [IllegalArgumentException] thrownBy(new Encoder.IoBundle(numberOfInputs))
			thrown.getMessage must include("arg=numberOfInputs")
		}}
	}

	private val numberOfInputs = Seq(1, 2, 3, 4, anyNumberOfInputs()).asTable("numberOfInputs")

	private def anyNumberOfInputs() = Random.between(1, 64)

	it must "have IO for the number of inputs passed to the constructor" in spinalContext {
		forAll(numberOfInputs) { (numberOfInputs: Int) => {
			val bundle = new Encoder.IoBundle(numberOfInputs)
			bundle.inputs.length must be(numberOfInputs)
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
			val bundle = new Encoder.IoBundle(numberOfInputs)
			bundle.output.getWidth must be(outputWidth)
		}}
	}
}
