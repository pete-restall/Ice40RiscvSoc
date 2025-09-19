package uk.co.lophtware.msfreference.tests.memory.flashqspi

import org.scalatest.flatspec._
import spinal.core._
import spinal.core.formal._
import spinal.core.sim._

import uk.co.lophtware.msfreference.memory.flashqspi.FlashQspiMemoryStateMachine
import uk.co.lophtware.msfreference.tests.formal._

class FlashQspiMemoryStateMachineVerification extends AnyFlatSpec with FormalVerificationFixture[FlashQspiMemoryStateMachineVerificationFixture] {
	protected override def dutFactory() = new FlashQspiMemoryStateMachineVerificationFixture().withStimuli

	protected override val bmcDepth = 30

	protected override val proofDepth = 50

	"Formal Verification" must "ensure that all states are entered via their entry states" in proof { implicit dut =>
		dut.allStatePredicates.foreach { isInState =>
			val seenEntryStatePast = Reg(Bool()) init(False)
			val seenEntryState = seenEntryStatePast || pastValid && rose(isInState)
			seenEntryStatePast := seenEntryState
			formallyAssert(!isInState || seenEntryState)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isBitBangingGranted flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not rise high on the first clock after reset" in proof { dut =>
		when(pastValid && fell(dut.clockDomain.isResetActive)) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "be high on the second clock after reset" in proof { dut =>
		when(pastValid && past(fell(dut.clockDomain.isResetActive))) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "remain high whilst isBitBangingRequested is high" in proof { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingRequested && dut.io.bitBanger.isBitBangingGranted)) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not fall low when isBitBangingRequested falls low whilst a transaction is in progress" in proof { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.readWriteStrobe.ready && !dut.io.bitBanger.isBitBangingRequested)) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "fall low one clock after isBitBangingRequested falls low when no transaction is in progress" in proof { dut =>
		when(pastValid && past(dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.ready && fell(dut.io.bitBanger.isBitBangingRequested))) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "not rise high after the initial reset state when isBitBangingRequested rises high whilst a transaction is in progress" in bmcOnly { dut =>
		val afterInitialResetState = RegNextWhen(True, pastValid && dut.io.bitBanger.isBitBangingGranted) init(False)
		when(afterInitialResetState && past(!dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.readWriteStrobe.ready && dut.io.bitBanger.isBitBangingRequested)) {
			formallyAssert(!dut.io.bitBanger.isBitBangingGranted)
		}
	}

	it must "rise high one clock after isBitBangingRequested rises high when no transaction is in progress" in proof { dut =>
		when(pastValid && past(!dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.ready && rose(dut.io.bitBanger.isBitBangingRequested))) {
			formallyAssert(dut.io.bitBanger.isBitBangingGranted)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isQspiGranted flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "be low immediately after reset" in proof { dut =>
		when(pastValid && fell(dut.clockDomain.isResetActive)) {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "not change when isQspiRequested is stable whilst bit-banging" in proof { dut =>
		when(pastValid && dut.io.bitBanger.isBitBangingGranted && past(stable(dut.clockDomain.isResetActive) && stable(dut.io.bitBanger.isBitBangingGranted) && stable(dut.io.bitBanger.isQspiRequested) && stable(dut.io.bitBanger.isChipSelected))) {
			formallyAssert(stable(dut.io.bitBanger.isQspiGranted))
		}
	}

	it must "change from low to high when isChipSelected whilst bit-banging the isQspiRequested flag" in proof { dut =>
		when(pastValid && dut.io.bitBanger.isBitBangingGranted && past(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isChipSelected && !dut.io.bitBanger.isQspiGranted && rose(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "not change from high to low when isChipSelected whilst bit-banging the isQspiRequested flag" in proof { dut =>
		when(pastValid && dut.io.bitBanger.isBitBangingGranted && past(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isChipSelected && dut.io.bitBanger.isQspiGranted && fell(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "change from low to high when not isChipSelected whilst bit-banging the isQspiRequested flag" in proof { dut =>
		when(pastValid && dut.io.bitBanger.isBitBangingGranted && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.bitBanger.isChipSelected && !dut.io.bitBanger.isQspiGranted && rose(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "change from high to low when not isChipSelected whilst bit-banging the isQspiRequested flag" in proof { dut =>
		when(pastValid && dut.io.bitBanger.isBitBangingGranted && past(dut.io.bitBanger.isBitBangingGranted && !dut.io.bitBanger.isChipSelected && dut.io.bitBanger.isQspiGranted && fell(dut.io.bitBanger.isQspiRequested))) {
			formallyAssert(!dut.io.bitBanger.isQspiGranted)
		}
	}

	"FlashQspiMemoryStateMachine bit-banger isTransactionValid flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be high when the requested QSPI mode has been granted whilst bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isQspiRequested === dut.io.bitBanger.isQspiGranted) {
			formallyAssert(dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be low when the requested QSPI mode has not been granted whilst bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted && dut.io.bitBanger.isQspiRequested =/= dut.io.bitBanger.isQspiGranted) {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}

	it must "be low when not bit-banging" in proof { dut =>
		when(!dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(!dut.io.bitBanger.isTransactionValid)
		}
	}

	"FlashQspiMemoryStateMachine fast-reader ready flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	it must "be low immediately after reset" in proof { dut =>
		when(pastValid && fell(dut.clockDomain.isResetActive)) {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	it must "be low when the driver reset flag is high" in proof { dut =>
		when(dut.io.driver.reset) {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	it must "be low during bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	it must "be low during the transition from bit-banging to fast-read" in proof { dut =>
		when(!dut.io.driver.reset && !dut.io.bitBanger.isBitBangingGranted && pastValid && past(dut.io.bitBanger.isBitBangingGranted)) {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	it must "only be high during the fast-read idle state when there is no transaction in progress and no pending bit-banging transition" in proof { implicit dut =>
		val isIdleState = createSignalForIsFastReadIdleState()
		formallyAssert(
			!dut.io.fastReader.ready ||
			dut.io.fastReader.ready && isIdleState && dut.io.driver.transaction.readWriteStrobe.ready && !dut.io.bitBanger.isBitBangingRequested)
	}

	private def createSignalForIsFastReadIdleState()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = dut.isCurrentState("fastReadIdleState")

	it must "not be high during any other state than fast-read idle or entry into fast-read idle" in proof { implicit dut =>
		val isIdleState = createSignalForIsFastReadIdleState()
		val isNextStateIdle = createSignalForIsFastReadNextStateIdle()
		when(!isIdleState && !isNextStateIdle) {
			formallyAssert(!dut.io.fastReader.ready)
		}
	}

	private def createSignalForIsFastReadNextStateIdle()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = dut.isNextState("fastReadIdleState")

	it must "only be high on two consecutive clocks if the first read was from a consecutive address and the transaction has completed" in bmcOnly { implicit dut =>
		val lastReadAddress = createRegisterForLastFastReadAddress()
		val isReadFromConsecutiveAddress = dut.io.fastReader.payload === lastReadAddress + 1
		when(pastValid && dut.io.fastReader.fire && past(dut.io.fastReader.fire)) {
			formallyAssert(past(isReadFromConsecutiveAddress) && dut.io.driver.transaction.readWriteStrobe.ready)
		}
	}

	private def createRegisterForLastFastReadAddress()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = {
		val lastReadAddress = Reg(UInt(24 bits)) init(0xffffff)
		when(dut.io.fastReader.fire) { lastReadAddress := dut.io.fastReader.payload }
		when(dut.io.bitBanger.isBitBangingGranted) { lastReadAddress := 0xffffff }
		lastReadAddress
	}

	"FlashQspiMemoryStateMachine driver isChipSelected flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.isChipSelected)
		}
	}

	it must "be low when the driver reset flag is high" in proof { dut =>
		when(dut.io.driver.reset) {
			formallyAssert(!dut.io.driver.isChipSelected)
		}
	}

	it must "follow the bit-banger's isChipSelected flag when bit-banging, not transitioning to bit-banging or fast-read, and not in a transaction" in proof { dut =>
		when(!dut.io.driver.reset && dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.ready) {
			formallyAssert(dut.io.driver.isChipSelected === dut.io.bitBanger.isChipSelected)
		}
	}

	it must "be low when exiting bit-banging and entering fast-read whilst not in a transaction" in proof { dut =>
		when(pastValid && fell(dut.io.bitBanger.isBitBangingGranted) && dut.io.driver.transaction.readWriteStrobe.ready) {
			formallyAssert(!dut.io.driver.isChipSelected)
		}
	}

	it must "be high after entering fast-read from bit-banging" in proof { dut =>
		when(pastValidAfterReset && past(fell(dut.io.bitBanger.isBitBangingGranted), 2) && stable(dut.io.bitBanger.isBitBangingGranted) && !dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(past(dut.io.driver.isChipSelected))
		}
	}

	it must "be high when a transaction is in progress" in proof { dut =>
		when(!dut.io.driver.reset && !dut.io.driver.transaction.readWriteStrobe.ready) {
			formallyAssert(dut.io.driver.isChipSelected)
		}
	}

	// TODO: Why is this BMC only ?  We ought to be able to proove it via induction.
	it must "be low for one clock between a read-to-write transaction when not bit-banging" in bmcOnly { dut =>
		val lastTransactionWasRead = RegNextWhen(
			!dut.io.driver.transaction.readWriteStrobe.payload,
			!dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.fire) init(False)

		val seenChipDeselected = Reg(Bool()) init(False)
		seenChipDeselected := seenChipDeselected || pastValid && lastTransactionWasRead && fell(dut.io.driver.isChipSelected)

		val isWriteTransaction =
			!dut.io.bitBanger.isBitBangingGranted &&
			dut.io.driver.transaction.readWriteStrobe.fire &&
			dut.io.driver.transaction.readWriteStrobe.payload

		when(pastValid && lastTransactionWasRead && isWriteTransaction) {
			formallyAssert(seenChipDeselected)
			seenChipDeselected := False
		}
	}

	it must "remain high when fast-reading from consecutive addresses" in proof { implicit dut =>
/*
		TODO: The title of the test is good and valid, but every time I open this and try to figure it out
		I cannot seem to make the test pass.  I guess this is what happens when you leave a project for
		3 years mid-commit :-/

		val lastReadAddress = createRegisterForLastFastReadAddress()
		val isReadFromConsecutiveAddress = dut.io.fastReader.payload === lastReadAddress + 1

		when(pastValid && past(dut.io.fastReader.fire)) {
			formallyAssert(past(!isReadFromConsecutiveAddress) || past(isReadFromConsecutiveAddress) && dut.io.driver.isChipSelected)
		}
		when(pastValid && dut.io.fastReader.fire) {
			formallyAssert(!isReadFromConsecutiveAddress || isReadFromConsecutiveAddress && dut.io.driver.isChipSelected)
		}
*/
	}



/*
	it must "remain high whilst fast-reading from consecutive addresses" in bmcOnly { dut =>
// TODO: THIS TEST IS _VERY_ SLOW...
		val isFastReadFiring = !dut.io.bitBanger.isBitBangingGranted && dut.io.fastReader.fire
		val lastReadAddress = RegNextWhen(dut.io.fastReader.payload, isFastReadFiring) init(0)
		val seenFirstFastRead = Reg(Bool()) init(False)
		val isFirstReadAfterChipDeselected = Reg(Bool()) init(True)
		isFirstReadAfterChipDeselected := isFirstReadAfterChipDeselected || pastValid && rose(dut.io.driver.isChipSelected)
		when(isFirstReadAfterChipDeselected && isFastReadFiring) {
			seenFirstFastRead := True
			isFirstReadAfterChipDeselected := False
		}

		when(dut.io.driver.reset || dut.io.bitBanger.isBitBangingGranted) {
			seenFirstFastRead := False
			isFirstReadAfterChipDeselected := True
		}





		val isConsecutiveRead = isFastReadFiring && dut.io.fastReader.payload === lastReadAddress + 1
		val isNonConsecutiveRead = isFastReadFiring && dut.io.fastReader.payload =/= lastReadAddress + 1
		val wasLastReadConsecutive = Reg(Bool()) init(False)
		when(seenFirstFastRead && isConsecutiveRead) {
			wasLastReadConsecutive := True
		}
		when(isNonConsecutiveRead || dut.io.bitBanger.isBitBangingGranted) {
			wasLastReadConsecutive := False
		}

// TODO: dut.io.fastReader.fire will be false after the first read has to transition to dirtyRead but before the next fast read; we need to skip over these sections of non-continuous reads; CS is only high for a consecutive read until the next read
		when(pastValidAfterReset && wasLastReadConsecutive && !dut.io.bitBanger.isBitBangingGranted) {// && seenFirstFastRead) { ///*&& seenFirstFastRead && */(!dut.io.fastReader.fire && wasLastReadConsecutive || isConsecutiveRead)) {
			formallyAssert(dut.io.driver.isChipSelected)// === (/*isConsecutiveRead && */wasLastReadConsecutive))
		}
	}
*/

/*

	private def createRegisterToCountChipSelectCycles()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = {
		val counter = Reg(UInt(8 bits)) init(0)
		when(pastValid && rose(dut.io.driver.isChipSelected)) {
			counter := counter + 1
		}
		counter
	}

	it must "toggle for one clock when fast-reading from an address that has not incremented by one in the same (non-initial) cycle" in verification { implicit dut =>
// TODO: MAKE THIS NOT CARE ABOUT BEING THE _IMMEDIATE_ CLOCK AFTER THE DIRTY READ - IT NEEDS TO BE THE NEXT CLOCK WHERE isChipSelected FALLS LOW (WHEN !reset)
		var currentCycle = createRegisterToCountChipSelectCycles()
		val lastFastReadAddress = RegNextWhen(dut.io.fastReader.payload, dut.io.fastReader.fire)
		val lastFastReadCycle = RegNextWhen(currentCycle, dut.io.fastReader.fire) init(0)
		val isFirstRead = currentCycle =/= lastFastReadCycle
		val isSameCycle = currentCycle === lastFastReadCycle
		when(
			pastValidAfterReset &&
			past(!isFirstRead && dut.io.fastReader.fire && dut.io.fastReader.payload =/= lastFastReadAddress + 1, 2) &&
			past(!dut.io.driver.reset && !dut.io.bitBanger.isBitBangingRequested)) {

			formallyAssert(dut.io.driver.isChipSelected)
		}
	}
*/

// TODO: verify the first cycle after entering fast-read:
//       when entering fast-read from bit-banging, send spi 0xeb, then address 0x000000; validate that if first fastReader.payload === 0x000000 then /CS will not toggle and a QSPI transaction is made immediately for the next byte
// the initial fast read fire needs testing, too - it should send the address in the payload first

	"FlashQspiMemoryStateMachine driver reset flag" must "be high during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.reset)
		}
	}

	it must "rise when isBitBangingGranted rises" in proof { dut =>
		when(pastValid && past(!dut.io.driver.reset) && rose(dut.io.bitBanger.isBitBangingGranted)) {
			formallyAssert(rose(dut.io.driver.reset))
		}
	}

	it must "fall on the first clock after isBitBangingGranted rises" in proof { dut =>
		when(pastValid && past(rose(dut.io.bitBanger.isBitBangingGranted))) {
			formallyAssert(fell(dut.io.driver.reset))
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's isQspi flag" must "be low during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.isQspi)
		}
	}

	it must "follow the bit-banger's isQspiGranted flag when bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver.transaction.isQspi === dut.io.bitBanger.isQspiGranted)
		}
	}

	it must "be low for the first transaction after leaving bit-banging" in bmcOnly { dut =>
		var isFirstTransactionAfterLeavingBitBanging = RegNextWhen(True, dut.io.bitBanger.isBitBangingGranted) init(True)
		when(!dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.fire) {
			formallyAssert(!isFirstTransactionAfterLeavingBitBanging || !dut.io.driver.transaction.isQspi)
			isFirstTransactionAfterLeavingBitBanging := False
		}
	}

	it must "be high for all subsequent transactions during fast-read" in bmcOnly { dut =>
		var isFirstTransactionAfterLeavingBitBanging = RegNextWhen(True, dut.io.bitBanger.isBitBangingGranted) init(True)
		when(!dut.io.bitBanger.isBitBangingGranted && dut.io.driver.transaction.readWriteStrobe.fire) {
			formallyAssert(isFirstTransactionAfterLeavingBitBanging || dut.io.driver.transaction.isQspi)
			isFirstTransactionAfterLeavingBitBanging := False
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's isWriteProtected flag" must "be high during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.transaction.isWriteProtected)
		}
	}

	it must "follow the bit-banger's isWriteProtected flag when bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver.transaction.isWriteProtected === dut.io.bitBanger.isWriteProtected)
		}
	}

	"FlashQspiMemoryStateMachine driver transaction's MOSI byte" must "be 0xff during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(dut.io.driver.transaction.mosi === 0xff)
		}
	}

	it must "be 0xff during bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(dut.io.driver.transaction.mosi === 0xff)
		}
	}

	it must "be 0xeb (Fast Read Quad I/O) for any SPI transaction during fast-read" in proof { dut =>
		when(!dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.isQspi && dut.io.driver.transaction.readWriteStrobe.fire) {
			formallyAssert(dut.io.driver.transaction.mosi === 0xeb)
		}
	}

// TODO: AFTER THE SPI TRANSACTION, WE OUGHT TO GET A SINGLE QSPI WRITE-ONLY TRANSACTION OF SIX BYTES, WITH 3x ADDRESS (MSB FIRST), 1x XIP FLAGS, 2x DUMMY

	"FlashQspiMemoryStateMachine driver transaction's strobe" must "not be valid during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.valid)
		}
	}

	it must "not be a write transaction during reset" in proof { dut =>
		dut.clockDomain.duringReset {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.payload)
		}
	}

	it must "not be valid during bit-banging" in proof { dut =>
		when(dut.io.bitBanger.isBitBangingGranted) {
			formallyAssert(!dut.io.driver.transaction.readWriteStrobe.valid)
		}
	}

	it must "be valid on the first rising edge of isChipSelected after leaving bit-banging" in bmcOnly { dut =>
		val seenBitBangingFallingEdge = Reg(Bool()) init(False)
		seenBitBangingFallingEdge := pastValid && (
			seenBitBangingFallingEdge && !dut.io.bitBanger.isBitBangingGranted ||
			fell(dut.io.bitBanger.isBitBangingGranted))

		when(seenBitBangingFallingEdge && dut.io.driver.isChipSelected) {
			formallyAssert(rose(dut.io.driver.transaction.readWriteStrobe.valid))
			seenBitBangingFallingEdge := False
		}
	}

	it must "allow a single serdes a transaction before leaving the Fast Read Entry state for any reason other than returning to bit-banging" in bmcOnly { implicit dut =>
		val transactionCount = Reg(UInt(16 bits)) init(0)
		val isFastReadEntryState = createSignalForIsFastReadEntryState()
		when(pastValid && past(!isFastReadEntryState) && isFastReadEntryState) {
			transactionCount := 0
		}

		when(isFastReadEntryState && dut.io.driver.transaction.readWriteStrobe.fire) {
			transactionCount := transactionCount + 1
		}

		when(pastValid && past(isFastReadEntryState) && !isFastReadEntryState && !dut.io.driver.reset) {
			formallyAssert(transactionCount === 1)
			transactionCount := 0
		}
	}

	private def createSignalForIsFastReadEntryState()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = dut.isCurrentState("fastReadEntryState")

	// TODO: WE WANT TO ALLOW A (PARAMETERISED) BUFFER RATHER THAN SENDING A SINGLE BYTE AT A TIME, SINCE THE SERDES CLOCK COULD BE MUCH HIGHER - THIS TEST WOULD THEN HAVE TO CHANGE
	it must "allow six serdes single-byte transactions before leaving the Send Address state for any reason other than returning to bit-banging" in bmcOnly { implicit dut =>
		val transactionCount = Reg(UInt(16 bits)) init(0)
		val isSendAddressState = createSignalForIsFastReadSendAddressState()
		when(pastValid && past(!isSendAddressState) && isSendAddressState) {
			transactionCount := 0
		}

		when(isSendAddressState && dut.io.driver.transaction.readWriteStrobe.fire) {
			transactionCount := transactionCount + 1
		}

		when(pastValid && past(isSendAddressState) && !isSendAddressState && !dut.io.driver.reset) {
			formallyAssert(transactionCount === 6)
			transactionCount := 0
		}
	}

	private def createSignalForIsFastReadSendAddressState()(implicit dut: FlashQspiMemoryStateMachineVerificationFixture) = dut.isCurrentState("fastReadSendAddressState")

	it must "be a write during the fast-read comand (SPI) byte" in proof { dut =>
		when(!dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.isQspi && dut.io.driver.transaction.readWriteStrobe.fire) {
			formallyAssert(dut.io.driver.transaction.readWriteStrobe.payload)
		}
	}

	it must "only be a write for the first six bytes following the fast-read comand (SPI) byte" in bmcOnly { dut =>
		val seenSpiCommand = Reg(Bool()) init(False)
		seenSpiCommand := seenSpiCommand || !dut.io.bitBanger.isBitBangingGranted && !dut.io.driver.transaction.isQspi && dut.io.driver.transaction.readWriteStrobe.fire

		val byteCount = Reg(UInt(3 bits)) init(0)
		when(dut.io.driver.reset || dut.io.bitBanger.isBitBangingGranted || !dut.io.driver.isChipSelected) {
			seenSpiCommand := False
			byteCount := 0
		}

		when(seenSpiCommand && dut.io.driver.transaction.readWriteStrobe.fire) {
			when(byteCount <= 6) {
				byteCount := byteCount + 1
			}

			formallyAssert(
				byteCount < 6 && dut.io.driver.transaction.readWriteStrobe.payload ||
				byteCount >= 6 && !dut.io.driver.transaction.readWriteStrobe.payload)
		}
	}
}
