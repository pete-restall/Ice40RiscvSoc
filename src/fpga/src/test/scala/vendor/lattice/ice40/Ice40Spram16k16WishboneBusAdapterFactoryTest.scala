package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._

import uk.co.lophtware.msfreference.tests.simulation._
import uk.co.lophtware.msfreference.vendor.lattice.ice40.{Ice40Spram16k16, Ice40Spram16k16WishboneBusAdapter}

class Ice40Spram16k16WishboneBusAdapterFactoryTest extends AnyFlatSpec
	with SimulationFixture[Ice40Spram16k16WishboneBusAdapterFactoryFixture]
	with TableDrivenPropertyChecks {

	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFactoryFixture()

	"Ice40Spram16k16WishboneBusAdapter companion's apply() method" must "not accept a null SPRAM" in simulator { fixture =>
		val thrown = the [IllegalArgumentException] thrownBy Ice40Spram16k16WishboneBusAdapter(null)
		thrown.getMessage must (include("arg=spram") and include("null"))
	}

	it must "wire the SPRAM's AD lines" in simulator { fixture =>
		val address = fixture.anyAddress()
		fixture.io.adapter.ADR #= address
		sleep(1)
		fixture.io.spram.AD.toInt must be(address)
	}

	it must "wire the SPRAM's DI lines" in simulator { fixture =>
		val data = fixture.anyData()
		fixture.io.adapter.DAT_MOSI #= data
		sleep(1)
		fixture.io.spram.DI.toInt must be(data)
	}

	it must "wire the SPRAM's MASKWE lines" in simulator { fixture =>
		val mask = fixture.anyWriteMask()
		fixture.io.adapter.SEL #= mask
		sleep(1)
		fixture.io.spram.MASKWE.toInt must be(mask)
	}

	private val booleans = tableFor("value", List(true, false))

	private def tableFor[A](header: (String), values: Iterable[A]) = Table(header) ++ values

	it must "wire the SPRAM's WE line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) => {
			fixture.io.adapter.WE #= value
			sleep(1)
			fixture.io.spram.WE.toBoolean must be(value)
		}}
	}

	it must "wire the SPRAM's CS line" in simulator { fixture =>
		forAll(booleans) { (value: Boolean) => {
			fixture.io.adapter.CYC #= value
			sleep(1)
			fixture.io.spram.CS.toBoolean must be(value)
		}}
	}
}
