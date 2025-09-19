package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import org.scalatest.AppendedClues._
import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Ebram4k16WishboneBusAdapter

class Ice40Ebram4k16WishboneBusAdapterSimulationTest extends AnyFlatSpec with LightweightSimulationFixture[Ice40Ebram4k16WishboneBusAdapterFixture] {
	protected override def dutFactory() = new Ice40Ebram4k16WishboneBusAdapterFixture()

	"Ice40Ebram4k16WishboneBusAdapter" must "not assert EBRAM CER when reading and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CER.toBoolean must be(false)
	}

	it must "not assert EBRAM CER when writing and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CER.toBoolean must be(false)
	}

	it must "assert EBRAM CER asynchronously when reading and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.ebram.CER.toBoolean must be(true)
	}

	it must "not assert EBRAM CER asynchronously when writing and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.ebram.CER.toBoolean must be(false)
	}

	it must "unassert EBRAM CER asynchronously when Wishbone CYC goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CER.toBoolean must be(false)
	}

	it must "not assert EBRAM CEW when writing and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CEW.toBoolean must be(false)
	}

	it must "not assert EBRAM CEW when reading and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CEW.toBoolean must be(false)
	}

	it must "assert EBRAM CEW asynchronously when writing and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.ebram.CEW.toBoolean must be(true)
	}

	it must "not assert EBRAM CEW asynchronously when reading and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.ebram.CEW.toBoolean must be(false)
	}

	it must "unassert EBRAM CEW asynchronously when Wishbone CYC goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.ebram.CEW.toBoolean must be(false)
	}

	it must "set EBRAM ADR asynchronously when Wishbone ADR is set" in simulator { fixture =>
		for (address <- List(0x0000, fixture.io.wishbone.ADR.maxValue.toInt, fixture.anyAddress())) {
			fixture.io.wishbone.ADR #= address
			sleep(1)
			fixture.io.ebram.ADR.toInt must be(address)
		}
	}

	it must "set EBRAM ADW asynchronously when Wishbone ADR is set" in simulator { fixture =>
		for (address <- List(0x0000, fixture.io.wishbone.ADR.maxValue.toInt, fixture.anyAddress())) {
			fixture.io.wishbone.ADR #= address
			sleep(1)
			fixture.io.ebram.ADW.toInt must be(address)
		}
	}

	it must "set EBRAM DI asynchronously when Wishbone DAT_MOSI is set" in simulator { fixture =>
		for (data <- List(0x0000, (1 << fixture.io.wishbone.DAT_MOSI.getWidth) - 1, fixture.anyData())) {
			fixture.io.wishbone.DAT_MOSI #= data
			sleep(1)
			fixture.io.ebram.DI.toInt must be(data)
		}
	}

	it must "set Wishbone DAT_MISO asynchronously when EBRAM DO is set" in simulator { fixture =>
		for (data <- List(0x0000, (1 << fixture.io.ebram.DO.getWidth) - 1, fixture.anyData())) {
			fixture.io.ebram.DO #= data
			sleep(1)
			fixture.io.wishbone.DAT_MISO.toInt must be(data)
		}
	}

	it must "assert EBRAM WE asynchronously when writing and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.ebram.WE.toBoolean must be(true)
	}

	it must "not assert EBRAM WE asynchronously when reading and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.ebram.WE.toBoolean must be(false)
	}

	it must "not assert EBRAM WE asynchronously when writing and Wishbone STB is not high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.WE.toBoolean must be(false)
	}

	it must "unassert EBRAM WE asynchronously when writing and Wishbone STB goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.WE.toBoolean must be(false)
	}

	it must "unassert EBRAM WE asynchronously when reading and Wishbone STB goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.WE.toBoolean must be(false)
	}

	it must "assert EBRAM RE asynchronously when writing and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.ebram.RE.toBoolean must be(true)
	}

	it must "assert EBRAM RE asynchronously when reading and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.ebram.RE.toBoolean must be(true)
	}

	it must "not assert EBRAM RE asynchronously when writing and Wishbone STB is not high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.RE.toBoolean must be(false)
	}

	it must "unassert EBRAM RE asynchronously when writing and Wishbone STB goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.RE.toBoolean must be(false)
	}

	it must "unassert EBRAM RE asynchronously when reading and Wishbone STB goes low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.ebram.RE.toBoolean must be(false)
	}

	it must "set the inverted EBRAM MASK_N asynchronously when Wishbone SEL is set" in simulator { fixture =>
		val maximumSel = (1 << fixture.io.wishbone.SEL.getWidth) - 1
		for (bitCombination <- 0 to maximumSel) {
			fixture.io.wishbone.SEL #= bitCombination
			sleep(1)
			fixture.io.ebram.MASK_N.toInt must be((~bitCombination) & maximumSel)
		}
	}

	it must "not assert Wishbone ACK when reading and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= false
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when writing and Wishbone CYC is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= false
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when reading and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when writing and Wishbone CYC goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when reading and Wishbone STB is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when writing and Wishbone STB is low" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when reading and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "assert Wishbone ACK when writing and Wishbone STB goes high" in simulator { fixture =>
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(true)
	}

	it must "assert Wishbone ACK one cycle after Wishbone STB goes high when reading" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false) withClue "at first assertion"
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at second assertion"
	}

	it must "unassert Wishbone ACK one cycle after Wishbone STB goes low when reading" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.STB #= false
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at first assertion"
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false) withClue "at second assertion"
	}

	it must "unassert Wishbone ACK immediately after Wishbone STB goes low when writing" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.STB #= false
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "assert Wishbone ACK one cycle after Wishbone STB goes high when reading and then maintain high when writing" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.WE #= false
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false) withClue "at first assertion"
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at second assertion"
		fixture.io.wishbone.WE #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at third assertion"
	}

	it must "assert Wishbone ACK immediately after Wishbone STB goes high when writing and then re-assert one cycle later when reading" in simulator { fixture =>
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.WE #= true
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at first assertion"
		fixture.io.wishbone.WE #= false
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false) withClue "at second assertion"
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true) withClue "at third assertion"
	}
}
