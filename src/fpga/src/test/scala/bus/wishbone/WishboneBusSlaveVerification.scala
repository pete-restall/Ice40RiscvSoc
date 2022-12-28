package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.tests.formal._

class WishboneBusSlaveVerification[TDut <: Component with IHaveWishboneSlave](slaveDutFactory: () => TDut) extends AnyFlatSpec with FormalVerificationFixture[TDut] {
	protected override def dutFactory() = slaveDutFactory()

	"Wishbone Master interface" must "hold CYC inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.asWishboneSlave.CYC)
		}
	}

	it must "hold WE inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.asWishboneSlave.WE)
		}
	}

	it must "hold STB inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssume(!dut.asWishboneSlave.STB)
		}
	}

	it must "not activate CYC within the first clock edge after reset" in verification { dut =>
		formallyAssume(!pastValid && !dut.asWishboneSlave.CYC)
		formallyAssume(pastValid && past(dut.clockDomain.isResetActive) && !dut.asWishboneSlave.CYC)
	}

	it must "activate STB only during a cycle" in verification { dut =>
		formallyAssume(!dut.asWishboneSlave.STB || (dut.asWishboneSlave.STB && dut.asWishboneSlave.CYC))
	}

	it must "not change WE during STB" in verification { dut =>
		when (pastValid && past(dut.asWishboneSlave.STB)) {
			formallyAssume(dut.asWishboneSlave.STB === past(dut.asWishboneSlave.STB))
		}
	}

	it must "not change STB during STALL" in verification { dut =>
		if (dut.asWishboneSlave.STALL != null) {
			when (pastValid && past(dut.asWishboneSlave.STALL)) {
				formallyAssume(dut.asWishboneSlave.STB === past(dut.asWishboneSlave.STB))
			}
		}
	}

	it must "not change WE during STALL" in verification { dut =>
		if (dut.asWishboneSlave.STALL != null) {
			when (pastValid && past(dut.asWishboneSlave.STALL)) {
				formallyAssume(dut.asWishboneSlave.WE === past(dut.asWishboneSlave.WE))
			}
		}
	}

	it must "not change ADR during STALL" in verification { dut =>
		if (dut.asWishboneSlave.STALL != null) {
			when (pastValid && past(dut.asWishboneSlave.STALL)) {
				formallyAssume(dut.asWishboneSlave.ADR === past(dut.asWishboneSlave.ADR))
			}
		}
	}

	it must "not change MOSI during STALL" in verification { dut =>
		if (dut.asWishboneSlave.STALL != null) {
			when (pastValid && past(dut.asWishboneSlave.STALL)) {
				formallyAssume(dut.asWishboneSlave.DAT_MOSI === past(dut.asWishboneSlave.DAT_MOSI))
			}
		}
	}

	"Wishbone Slave interface" must "hold ACK inactive in reset" in verification { dut =>
		when (dut.clockDomain.isResetActive) {
			formallyAssert(!dut.asWishboneSlave.ACK)
		}
	}

	it must "hold ERR inactive in reset" in verification { dut =>
		if (dut.asWishboneSlave.ERR != null) {
			when (dut.clockDomain.isResetActive) {
				formallyAssert(!dut.asWishboneSlave.ERR)
			}
		}
	}

	it must "hold RTY inactive in reset" in verification { dut =>
		if (dut.asWishboneSlave.RTY != null) {
			when (dut.clockDomain.isResetActive) {
				formallyAssert(!dut.asWishboneSlave.RTY)
			}
		}
	}

	it must "not assert ACK and ERR at the same time" in verification { dut =>
		if (dut.asWishboneSlave.ERR != null) {
			formallyAssert((!dut.asWishboneSlave.ACK && !dut.asWishboneSlave.ERR) || (dut.asWishboneSlave.ACK ^ dut.asWishboneSlave.ERR))
		}
	}

	it must "not assert ACK and RTY at the same time" in verification { dut =>
		if (dut.asWishboneSlave.RTY != null) {
			formallyAssert((!dut.asWishboneSlave.ACK && !dut.asWishboneSlave.RTY) || (dut.asWishboneSlave.ACK ^ dut.asWishboneSlave.RTY))
		}
	}

	it must "not assert ERR and RTY at the same time" in verification { dut =>
		if (dut.asWishboneSlave.ERR != null && dut.asWishboneSlave.RTY != null) {
			formallyAssert((!dut.asWishboneSlave.ERR && !dut.asWishboneSlave.RTY) || (dut.asWishboneSlave.ERR ^ dut.asWishboneSlave.RTY))
		}
	}

	it must "not assert ACK without CYC" in verification { dut =>
		formallyAssert(!dut.asWishboneSlave.ACK || (dut.asWishboneSlave.ACK && dut.asWishboneSlave.CYC))
	}

	it must "not assert ERR without CYC" in verification { dut =>
		if (dut.asWishboneSlave.ERR != null) {
			formallyAssert(!dut.asWishboneSlave.ERR || (dut.asWishboneSlave.ERR && dut.asWishboneSlave.CYC))
		}
	}

	it must "not assert RTY without CYC" in verification { dut =>
		if (dut.asWishboneSlave.RTY != null) {
			formallyAssert(!dut.asWishboneSlave.RTY || (dut.asWishboneSlave.RTY && dut.asWishboneSlave.CYC))
		}
	}

	it must "not assert ACK without a previous STB" in verification { dut =>
		formallyAssert(!dut.asWishboneSlave.ACK || (pastValid && dut.asWishboneSlave.ACK && past(dut.asWishboneSlave.STB)))
	}

	it must "not assert ERR without a previous STB" in verification { dut =>
		if (dut.asWishboneSlave.ERR != null) {
			formallyAssert(!dut.asWishboneSlave.ERR || (pastValid && dut.asWishboneSlave.ERR && past(dut.asWishboneSlave.STB)))
		}
	}

	it must "not assert RTY without a previous STB" in verification { dut =>
		if (dut.asWishboneSlave.RTY != null) {
			formallyAssert(!dut.asWishboneSlave.RTY || (pastValid && dut.asWishboneSlave.RTY && past(dut.asWishboneSlave.STB)))
		}
	}

	it must "contain the same number of ACKs as STBs when STB is not active on the last clock of CYC" in verification { implicit dut =>
		val numberOfStbs = Reg(UInt(16 bits)) init(0)
		val numberOfAcks = Reg(UInt(16 bits)) init(0)

		when (cycleStarted) {
			numberOfAcks := 0
			numberOfStbs := 0
		}

		when (dut.asWishboneSlave.CYC) {
			when (dut.asWishboneSlave.STB) {
				numberOfStbs := numberOfStbs + 1
			}

			when (dut.asWishboneSlave.ACK) {
				numberOfAcks := numberOfAcks + 1
			}
		}

		when (pastValid && cycleEnded && !cycleEndedWithStb) {
			formallyAssert(numberOfAcks === numberOfStbs)
		}
	}

	private def cycleStarted(implicit dut: TDut) = pastValid && dut.asWishboneSlave.CYC && !past(dut.asWishboneSlave.CYC)

	private def cycleEndedWithStb(implicit dut: TDut) = cycleEnded && past(dut.asWishboneSlave.STB)

	private def cycleEnded(implicit dut: TDut) = pastValid && past(dut.asWishboneSlave.CYC) && !dut.asWishboneSlave.CYC

	it must "contain the one less ACK than there were STBs when STB is active on the last clock of CYC" in verification { implicit dut =>
		val numberOfStbs = Reg(UInt(16 bits)) init(0)
		val numberOfAcks = Reg(UInt(16 bits)) init(0)

		when (cycleStarted) {
			numberOfAcks := 0
			numberOfStbs := 0
		}

		when (dut.asWishboneSlave.CYC) {
			when (dut.asWishboneSlave.STB) {
				numberOfStbs := numberOfStbs + 1
			}

			when (dut.asWishboneSlave.ACK) {
				numberOfAcks := numberOfAcks + 1
			}
		}

		when (pastValid && cycleEndedWithStb) {
			formallyAssert(numberOfAcks === numberOfStbs - 1)
		}
	}

	it must "only assert STALL during STB" in verification { dut =>
		if (dut.asWishboneSlave.STALL != null) {
			formallyAssert(dut.asWishboneSlave.STB)
		}
	}

	// TODO: MUST ASSERT THAT THE SLAVE MAINTAINS MISO DURING AN ACK
}
