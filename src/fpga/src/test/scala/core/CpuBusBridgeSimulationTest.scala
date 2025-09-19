package net.restall.ice40riscvsoc.tests.core

import scala.util.Random

import org.scalatest.Inspectors
import org.scalatest.flatspec._
import org.scalatest.matchers.must.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.wishbone.Wishbone

import net.restall.ice40riscvsoc.core.CpuBusBridge
import net.restall.ice40riscvsoc.tests.IterableTableExtensions._
import net.restall.ice40riscvsoc.tests.simulation._

// TODO: MAKE THIS PARAMETERISED WITH THE TWO BUS CONFIGURATIONS AND THEN CREATE A SUITE TO EXERCISE THE VARIOUS COMBINATIONS
class CpuBusBridgeSimulationTest extends AnyFlatSpec with LightweightSimulationFixture[CpuBusBridgeFixture] with TableDrivenPropertyChecks with Inspectors {

	protected override def dutFactory() = new CpuBusBridgeFixture()

	"CpuBridge executable bus" must "have CYC that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheDbusToExecutableBridgeBusIoLine(_.CYC)
	}

	private val booleans = Seq(true, false).asTable("value")

	private def mustHaveIoLineThatAsynchronouslyFollowsTheDbusToExecutableBridgeBusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.devices.dbusToExecutableBridge) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.devices.executable).toBoolean must be(value)
		}
	}

	it must "have STB that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheDbusToExecutableBridgeBusIoLine(_.STB)
	}

	it must "have WE that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheDbusToExecutableBridgeBusIoLine(_.WE)
	}

	it must "have ADR that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheDbusToExecutableBridgeBusIoLines(_.ADR)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheDbusToExecutableBridgeBusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.devices.dbusToExecutableBridge)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)
		ioLineFrom(fixture.io.bridge.devices.executable).toLong must be(value)
	}

	it must "have SEL that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.devices.dbusToExecutableBridge.SEL != null) {
			mustHaveIoLinesThatAsynchronouslyFollowTheDbusToExecutableBridgeBusIoLines(_.SEL)
		}
	}

	it must "have DAT_MOSI that asynchronously follows the dbus-to-executable bridge bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheDbusToExecutableBridgeBusIoLines(_.DAT_MOSI)
	}

	"CpuBridge dbus-to-executable bridge" must "have ACK that asynchronously follows the executable bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheExecutableBusIoLine(_.ACK)
	}

	private def mustHaveIoLineThatAsynchronouslyFollowsTheExecutableBusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.devices.executable) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.devices.dbusToExecutableBridge).toBoolean must be(value)
		}
	}

	it must "have STALL that asynchronously follows the executable bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.devices.dbusToExecutableBridge.STALL != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheExecutableBusIoLine(_.STALL)
		}
	}

	it must "have ERR that asynchronously follows the executable bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.devices.dbusToExecutableBridge.ERR != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheExecutableBusIoLine(_.ERR)
		}
	}

	it must "have DAT_MISO that asynchronously follows the executable bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheExecutableBusIoLines(_.DAT_MISO)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheExecutableBusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.devices.executable)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)
		ioLineFrom(fixture.io.bridge.devices.dbusToExecutableBridge).toLong must be(value)
	}

	"CpuBridge device's data bus" must "have CYC that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuDbusIoLine(_.CYC)
	}

	private def mustHaveIoLineThatAsynchronouslyFollowsTheCpuDbusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.cpu.dbus) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.devices.dbus).toBoolean must be(value)
		}
	}

	it must "have STB that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuDbusIoLine(_.STB)
	}

	it must "have WE that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuDbusIoLine(_.WE)
	}

	it must "have ADR that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheCpuDbusIoLines(_.ADR)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheCpuDbusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.cpu.dbus)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)

		val subject = ioLineFrom(fixture.io.bridge.devices.dbus)
		val truncatedMask = (1l << subject.getWidth) - 1
		subject.toLong must be(value & truncatedMask)
	}

	it must "have SEL that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.dbus.SEL != null && fixture.io.bridge.devices.executable.SEL != null) {
			mustHaveIoLinesThatAsynchronouslyFollowTheCpuDbusIoLines(_.SEL)
		}
	}

	// TODO: WHEN cpu.dbus.SEL == null && devices.dbus.SEL != null
	// TODO: WHEN cpu.dbus.SEL != null && devices.dbus.SEL == null

	it must "have DAT_MOSI that asynchronously follows the CPU's data bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheCpuDbusIoLines(_.DAT_MOSI)
	}

	"CpuBridge CPU's data bus" must "have ACK that asynchronously follows the device's data bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheDataOnlyBusIoLine(_.ACK)
	}

	private def mustHaveIoLineThatAsynchronouslyFollowsTheDataOnlyBusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.devices.dbus) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.cpu.dbus).toBoolean must be(value)
		}
	}

	it must "have STALL that asynchronously follows the device's data bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.dbus.STALL != null && fixture.io.bridge.devices.dbus.STALL != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheDataOnlyBusIoLine(_.STALL)
		}
	}

	// TODO: WHEN cpu.dbus.STALL == null && devices.dbus.STALL != null
	// TODO: WHEN cpu.dbus.STALL != null && devices.dbus.STALL == null

	it must "have ERR that asynchronously follows the device's data bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.dbus.ERR != null && fixture.io.bridge.devices.dbus.ERR != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheDataOnlyBusIoLine(_.ERR)
		}
	}

	// TODO: WHEN cpu.dbus.ERR == null && devices.dbus.ERR != null
	// TODO: WHEN cpu.dbus.ERR != null && devices.dbus.ERR == null

	it must "have DAT_MISO that asynchronously follows the device's data bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheDataOnlyBusIoLines(_.DAT_MISO)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheDataOnlyBusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.devices.dbus)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)

		val subject = ioLineFrom(fixture.io.bridge.cpu.dbus)
		val truncatedMask = (1l << subject.getWidth) - 1
		subject.toLong must be(value & truncatedMask)
	}

	"CpuBridge device's instruction bus" must "have CYC that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuIbusIoLine(_.CYC)
	}

	private def mustHaveIoLineThatAsynchronouslyFollowsTheCpuIbusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.cpu.ibus) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.devices.ibus).toBoolean must be(value)
		}
	}

	it must "have STB that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuIbusIoLine(_.STB)
	}

	it must "have WE that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheCpuIbusIoLine(_.WE)
	}

	it must "have ADR that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheCpuIbusIoLines(_.ADR)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheCpuIbusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.cpu.ibus)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)

		val subject = ioLineFrom(fixture.io.bridge.devices.ibus)
		val truncatedMask = (1l << subject.getWidth) - 1
		subject.toLong must be(value & truncatedMask)
	}

	it must "have SEL that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.ibus.SEL != null && fixture.io.bridge.devices.ibus.SEL != null) {
			mustHaveIoLinesThatAsynchronouslyFollowTheCpuIbusIoLines(_.SEL)
		}
	}

	// TODO: WHEN ibus.SEL == null && device.ibus.SEL != null
	// TODO: WHEN ibus.SEL != null && device.ibus.SEL == null

	it must "have DAT_MOSI that asynchronously follows the CPU's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheCpuIbusIoLines(_.DAT_MOSI)
	}

	"CpuBridge CPU's instruction bus" must "have ACK that asynchronously follows the device's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLineThatAsynchronouslyFollowsTheDeviceIbusIoLine(_.ACK)
	}

	private def mustHaveIoLineThatAsynchronouslyFollowsTheDeviceIbusIoLine(ioLineFrom: Wishbone => Bool)(implicit fixture: CpuBusBridgeFixture) {
		forAll(booleans) { (value: Boolean) =>
			ioLineFrom(fixture.io.bridge.devices.ibus) #= value
			sleep(1)
			ioLineFrom(fixture.io.bridge.cpu.ibus).toBoolean must be(value)
		}
	}

	it must "have STALL that asynchronously follows the device's instruction bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.ibus.STALL != null && fixture.io.bridge.devices.ibus.STALL != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheDeviceIbusIoLine(_.STALL)
		}
	}

	// TODO: WHEN ibus.STALL == null && device.ibus.STALL != null
	// TODO: WHEN ibus.STALL != null && device.ibus.STALL == null

	it must "have ERR that asynchronously follows the device's instruction bus" in simulator { implicit fixture =>
		if (fixture.io.bridge.cpu.ibus.ERR != null && fixture.io.bridge.devices.ibus.ERR != null) {
			mustHaveIoLineThatAsynchronouslyFollowsTheDeviceIbusIoLine(_.ERR)
		}
	}

	// TODO: WHEN ibus.ERR == null && device.ibus.ERR != null
	// TODO: WHEN ibus.ERR != null && device.ibus.ERR == null

	it must "have DAT_MISO that asynchronously follows the device's instruction bus" in simulator { implicit fixture =>
		mustHaveIoLinesThatAsynchronouslyFollowTheDeviceIbusIoLines(_.DAT_MISO)
	}

	private def mustHaveIoLinesThatAsynchronouslyFollowTheDeviceIbusIoLines(ioLineFrom: Wishbone => BitVector)(implicit fixture: CpuBusBridgeFixture) {
		val ioLine = ioLineFrom(fixture.io.bridge.devices.ibus)
		val value = Random.nextLong(1l << ioLine.getWidth)
		ioLine #= value
		sleep(1)

		val subject = ioLineFrom(fixture.io.bridge.cpu.ibus)
		val truncatedMask = (1l << subject.getWidth) - 1
		subject.toLong must be(value & truncatedMask)
	}

	"CpuBridge's data bus map" must "allow each slave to be selected from the CPU's data bus" in simulator { fixture =>
		forAll(Random.shuffle(fixture.io.dbusSlaves.zipWithIndex)) { case (slave, index) =>
			fixture.io.bridge.cpu.dbus.ADR #= fixture.firstDbusSlaveAddress + index
			sleep(1)
			fixture.io.dbusSlaveSelects.toInt must be(fixture.dbusSlaveSelectMaskFor(index))
		}
	}

	it must "allow the executable bus to be selected via the dbus-to-executable bus bridge" in simulator { fixture =>
		fixture.io.bridge.cpu.dbus.ADR #= fixture.dbusToExecutableBridgeAddress
		sleep(1)
		fixture.io.dbusSlaveSelects.toInt must be(fixture.dbusSlaveSelectMaskForExecutableBus)
	}

	"CpuBridge's executable bus map" must "allow each executable slave to be selected from the CPU's data bus" in simulator { fixture =>
		forAll(Random.shuffle(fixture.io.executableSlaves.zipWithIndex)) { case (slave, index) =>
			fixture.io.bridge.devices.dbusToExecutableBridge.ADR #= fixture.firstExecutableSlaveAddressFromDbus + index
			sleep(1)
			fixture.io.executableFromDbusSlaveSelects.toInt must be(fixture.executableSlaveSelectMaskFor(index))
		}
	}

	it must "allow each executable slave to be selected from the CPU's instruction bus" in simulator { fixture =>
		forAll(Random.shuffle(fixture.io.executableSlaves.zipWithIndex)) { case (slave, index) =>
			fixture.io.bridge.cpu.ibus.ADR #= fixture.firstExecutableSlaveAddressFromIbus + index
			sleep(1)
			fixture.io.executableFromIbusSlaveSelects.toInt must be(fixture.executableSlaveSelectMaskFor(index))
		}
	}
}
