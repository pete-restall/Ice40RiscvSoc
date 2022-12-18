package uk.co.lophtware.msfreference.tests

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core.sim._

import uk.co.lophtware.msfreference.PriorityEncoder
import uk.co.lophtware.msfreference.tests.simulation._

class PriorityEncoderSimulationTest(numberOfInputs: Int) extends AnyFlatSpec with LightweightSimulationFixture[PriorityEncoderFixture] with TableDrivenPropertyChecks {
	protected override def dutFactory() = new PriorityEncoderFixture(numberOfInputs)

	"PriorityEncoder isValid" must "be false when all inputs are false" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		sleep(1)
		fixture.io.isValid.toBoolean must be(false)
	}

	it must "be true when all inputs are true" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= true)
		sleep(1)
		fixture.io.isValid.toBoolean must be(true)
	}

	it must "be true when one input is true" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		anyInputIn(fixture) #= true
		sleep(1)
		fixture.io.isValid.toBoolean must be(true)
	}

	private def anyInputIn(fixture: PriorityEncoderFixture) = fixture.io.inputs(Random.nextInt(fixture.io.inputs.length))

	"PriorityEncoder output" must "be 0 when all inputs are false" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		sleep(1)
		fixture.io.output.toInt must be(0)
	}

	private val inputIndices = tableFor("index", 0 until numberOfInputs)

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "be index of true input when only one input is true" in simulator { fixture =>
		forAll(inputIndices) { index =>
			fixture.io.inputs.zip(0 until numberOfInputs).foreach(x => x._1 #= x._2 == index)
			sleep(1)
			fixture.io.output.toInt must be(index)
		}
	}

	it must "be lowest index of true inputs when more than one input is true" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		forAll(inputIndices) { index =>
			fixture.io.inputs.zip(0 until numberOfInputs).foreach(x => x._1 #= x._2 >= index)
			sleep(1)
			fixture.io.output.toInt must be(index)
		}
	}
}
