package net.restall.ice40riscvsoc.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.sim._

import net.restall.ice40riscvsoc.tests.simulation._
import net.restall.ice40riscvsoc.vendor.lattice.ice40.Ice40Spram16k16WishboneBusAdapter

class Ice40Spram16k16WishboneBusAdapterSimulationTest extends AnyFlatSpec with LightweightSimulationFixture[Ice40Spram16k16WishboneBusAdapterFixture] {
	protected override def dutFactory() = new Ice40Spram16k16WishboneBusAdapterFixture()

	"Ice40Spram16k16WishboneBusAdapter Wishbone bus" must "not assert SPRAM CS when Wishbone CYC is low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(false)
	}

	it must "assert SPRAM CS asynchronously when Wishbone CYC goes high" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(true)
	}

	it must "unassert SPRAM CS asynchronously when Wishbone CYC goes low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.spram.CS.toBoolean must be(false)
	}

	it must "set SPRAM AD asynchronously when Wishbone ADR is set" in simulator { fixture =>
		fixture.reset()
		for (address <- List(0x0000, fixture.io.wishbone.ADR.maxValue.toInt, fixture.anyAddress())) {
			fixture.io.wishbone.ADR #= address
			sleep(1)
			fixture.io.spram.AD.toInt must be(address)
		}
	}

	it must "set SPRAM DI asynchronously when Wishbone DAT_MOSI is set" in simulator { fixture =>
		fixture.reset()
		for (data <- List(0x0000, (1 << fixture.io.wishbone.DAT_MOSI.getWidth) - 1, fixture.anyData())) {
			fixture.io.wishbone.DAT_MOSI #= data
			sleep(1)
			fixture.io.spram.DI.toInt must be(data)
		}
	}

	it must "set Wishbone DAT_MISO asynchronously when SPRAM DO is set" in simulator { fixture =>
		fixture.reset()
		for (data <- List(0x0000, (1 << fixture.io.spram.DO.getWidth) - 1, fixture.anyData())) {
			fixture.io.spram.DO #= data
			sleep(1)
			fixture.io.wishbone.DAT_MISO.toInt must be(data)
		}
	}

	it must "assert SPRAM WE asynchronously when Wishbone WE and STB go high" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= true
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(true)
	}

	it must "not assert SPRAM WE when Wishbone WE is high and STB is low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= false
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(false)
	}

	it must "not assert SPRAM WE when Wishbone WE is low and STB is high" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= true
		fixture.io.wishbone.WE #= false
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(false)
	}

	it must "unassert SPRAM WE asynchronously when Wishbone WE goes low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= true
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.wishbone.WE #= false
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(false)
	}

	it must "unassert SPRAM WE asynchronously when Wishbone STB goes low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= true
		fixture.io.wishbone.WE #= true
		sleep(1)
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.spram.WE.toBoolean must be(false)
	}

	it must "set SPRAM MASKWE asynchronously when Wishbone SEL is set" in simulator { fixture =>
		fixture.reset()
		for (nybbleCombination <- 0 to (1 << fixture.io.wishbone.SEL.getWidth) - 1) {
			fixture.io.wishbone.SEL #= nybbleCombination
			sleep(1)
			fixture.io.spram.MASKWE.toInt must be(nybbleCombination)
		}
	}

	it must "not assert Wishbone ACK when Wishbone CYC is low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.CYC #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone CYC goes high" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.CYC #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone STB is low" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= false
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "not assert Wishbone ACK when Wishbone STB goes high" in simulator { fixture =>
		fixture.reset()
		fixture.io.wishbone.STB #= true
		sleep(1)
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}

	it must "assert Wishbone ACK one cycle after Wishbone STB goes high" in simulator { fixture =>
		fixture.reset()
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false)
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true)
	}

	it must "unassert Wishbone ACK one cycle after Wishbone STB goes low" in simulator { fixture =>
		fixture.reset()
		fixture.clockDomain.forkStimulus(period=10)
		fixture.io.wishbone.CYC #= true
		fixture.io.wishbone.STB #= true
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.STB #= false
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(true)
		fixture.clockDomain.waitSampling()
		fixture.io.wishbone.ACK.toBoolean must be(false)
	}
}
