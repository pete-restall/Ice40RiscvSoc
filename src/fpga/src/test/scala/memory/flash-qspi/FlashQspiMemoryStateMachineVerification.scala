package uk.co.lophtware.msfreference.tests.memory.flashqspi

import org.scalatest.flatspec._
import spinal.core._
import spinal.core.formal._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemoryStateMachine
import uk.co.lophtware.msfreference.tests.formal._

class FlashQspiMemoryStateMachineVerification extends AnyFlatSpec with FormalVerificationFixture[FlashQspiMemoryStateMachineVerificationFixture] {
	protected override def dutFactory() = new FlashQspiMemoryStateMachineVerificationFixture().withStimuli

	"FlashQspiMemoryStateMachine transaction strobe" should "not have both valid and ready flags high for more than one consecutive clock" in verification { dut =>
		when(dut.io.driver.transaction.readWriteStrobe.valid && dut.io.driver.transaction.readWriteStrobe.ready) {
			formallyAssume(!dut.io.driver.transaction.readWriteStrobe.ready)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isBitBangingGranted flag" must "be low during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not rise high on the first clock after reset" in verification { dut =>
		when(pastValid && fell(dut.clockDomain.isResetActive)) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "be high on the second clock after reset" in verification { dut =>
		when(pastValid && past(fell(dut.clockDomain.isResetActive))) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "remain high whilst isBitBangingRequested is high" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingRequested && dut.io.bitBanger.isBitBangingGranted)) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not fall low when isBitBangingRequested falls low whilst a transaction is in progress" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.readWriteStrobe.ready && !dut.io.bitBanger.isBitBangingRequested)) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "fall low one clock after isBitBangingRequested falls low when no transaction is in progress" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.ready && fell(dut.io.bitBanger.isBitBangingRequested))) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not rise high after the initial reset state when isBitBangingRequested rises high whilst a transaction is in progress" in verification { dut =>
		val afterInitialResetState = RegNextWhen(True, pastValid && dut.io.bitBanger.isBitBangingGranted) init(False)
		when(afterInitialResetState && past(!dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.readWriteStrobe.ready && dut.io.bitBanger.isBitBangingRequested)) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "rise high one clock after isBitBangingRequested rises high when no transaction is in progress" in verification { dut =>
		when(pastValid && past(!dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.ready && rose(dut.io.bitBanger.isBitBangingRequested))) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isQspiGranted flag" must "be low during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "be low immediately after reset" in verification { dut =>
		when(pastValid && fell(dut.clockDomain.isResetActive)) {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "not change when isQspiRequested is stable whilst bit-banging" in verification { dut =>
		when(pastValid && past(stable(dut.clockDomain.isResetActive) && stable(dut.io.bitBanger.isBitBangingGranted) && stable(dut.io.bitBanger.isQspiRequested) && stable(dut.io.bitBanger._Cs))) {
			formallyAssert(stable(dut.io.bitBanger.isQspiGranted))
		}
	}

	it must "change from low to high when /CS is low whilst bit-banging the isQspiRequested flag" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.bitBanger._Cs && !dut.io.bitBanger.isQspiGranted && rose(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "not change from high to low when /CS is low whilst bit-banging the isQspiRequested flag" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.bitBanger._Cs && dut.io.bitBanger.isQspiGranted && fell(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "change from low to high when /CS is high whilst bit-banging the isQspiRequested flag" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger._Cs && !dut.io.bitBanger.isQspiGranted && rose(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "change from high to low when /CS is high whilst bit-banging the isQspiRequested flag" in verification { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger._Cs && dut.io.bitBanger.isQspiGranted && fell(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isTransactionValid flag" must "be low during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be high when the requested QSPI mode has been granted whilst bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isQspiRequested === dut.io.bitBanger.isQspiGranted) {
			formallyAssert(dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be low when the requested QSPI mode has not been granted whilst bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isQspiRequested =/= dut.io.bitBanger.isQspiGranted) {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be low when not bit-banging" in verification { dut =>
		when(!dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}
	"FlashQspiMemoryStateMachine driver /CS line" must "be high during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver._Cs)
		}
	}

	it must "follow the bit-banger's /CS when bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver._Cs === dut.io.bitBanger._Cs)
		}
	}

	"FlashQspiMemoryStateMachine driver reset line" must "be high during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.reset)
		}
	}

	it must "rise when isBitBangingGranted rises" in verification { dut =>
		when(pastValid && past(!dut.io.driver.reset) && rose(dut.io.bitBanger.isBitBangingGranted)) {
			formallyAssert(rose(dut.io.driver.reset))
		}
	}

	it must "fall on the first clock after isBitBangingGranted rises" in verification { dut =>
		when(pastValid && past(rose(dut.io.bitBanger.isBitBangingGranted))) {
			formallyAssert(fell(dut.io.driver.reset))
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's isQspi flag" must "be low during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.isQspi)
		}
	}

	it must "follow the bit-banger's isQspiGranted flag when bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver.transaction.isQspi === dut.io.bitBanger.isQspiGranted)
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's isWriteProtected flag" must "be high during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.transaction.isWriteProtected)
		}
	}

	it must "follow the inverse of the bit-banger's _Wp flag when bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver.transaction.isWriteProtected === !dut.io.bitBanger._Wp)
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's MOSI byte" must "be zero during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.transaction.mosi === 0)
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's strobe" must "not be valid during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.valid)
		}
	}

	it must "not be a write transaction during reset" in verification { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.payload)
		}
	}

	it must "not be valid during bit-banging" in verification { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.valid)
		}
	}
}
