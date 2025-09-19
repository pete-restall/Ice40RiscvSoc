package net.restall.ice40riscvsoc.tests.multiplexing

import scala.util.Random

import org.scalatest.flatspec._
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.multiplexing.Decoder
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

class DecoderSimulationTest(inputWidth: BitCount, dutCreatedViaApplyFactory: Boolean) extends AnyFlatSpec
	with LightweightSimulationFixture[DecoderFixture]
	with TableDrivenPropertyChecks
	with Inspectors {

	protected override def dutFactory() = new DecoderFixture(inputWidth, dutCreatedViaApplyFactory)

	private val inputValues = Random.shuffle(Seq.range(0, inputWidth.value)).asTable("value")

	"Decoder outputs" must "all be false except the single true value corresponding to the selected input" in simulator { fixture =>
		forAll(inputValues) { (value: Int) =>
			fixture.io.input #= value
			sleep(1)
			forAll(fixture.io.outputs.zipWithIndex) { case(output, index) => output.toBoolean must be(index == value) }
		}
	}
}
