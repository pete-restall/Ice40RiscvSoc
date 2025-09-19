package net.restall.ice40riscvsoc.tests.multiplexing

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.multiplexing.SimpleEncoder
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class SimpleEncoderSimulationTest(numberOfInputs: Int, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[SimpleEncoderFixture]
	with TableDrivenPropertyChecks {

	protected override def dutFactory() = new SimpleEncoderFixture(numberOfInputs, dutCreatedViaApplyFactory)

	"SimpleEncoder isValid" must "be false when all inputs are false" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		sleep(1)
		fixture.io.isValid.toBoolean must be(false)
	}

	it must "be false when there is more than one input and they are all true" in simulator { fixture =>
		if (numberOfInputs > 1) {
			fixture.io.inputs.foreach(input => input #= true)
			sleep(1)
			fixture.io.isValid.toBoolean must be(false)
		}
	}

	it must "be true when one input is true" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		anyInputIn(fixture) #= true
		sleep(1)
		fixture.io.isValid.toBoolean must be(true)
	}

	private def anyInputIn(fixture: SimpleEncoderFixture) = fixture.io.inputs(Random.nextInt(fixture.io.inputs.length))

	"SimpleEncoder output" must "be 0 when all inputs are false" in simulator { fixture =>
		fixture.io.inputs.foreach(input => input #= false)
		sleep(1)
		fixture.io.output.toInt must be(0)
	}

	private val inputIndices = (0 until numberOfInputs).asTable("index")

	it must "be index of true input when only one input is true" in simulator { fixture =>
		forAll(inputIndices) { index =>
			fixture.io.inputs.zip(0 until numberOfInputs).foreach(x => x._1 #= x._2 == index)
			sleep(1)
			fixture.io.output.toInt must be(index)
		}
	}

	it must "be 0 when there is more than one input and more than one of them is true" in simulator { fixture =>
		if (numberOfInputs > 1) {
			val numberOfTrueInputs = Random.between(2, numberOfInputs + 1)
			val moreThanOneTrueInput = Random.shuffle(Seq.fill(numberOfTrueInputs) { true } ++ Seq.fill(numberOfInputs - numberOfTrueInputs) { false })
			fixture.io.inputs.zipWithIndex.foreach { x => x._1 #= moreThanOneTrueInput(x._2) }
			sleep(1)
			fixture.io.output.toInt must be(0)
		}
	}
}
