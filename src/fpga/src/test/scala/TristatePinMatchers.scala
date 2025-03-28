package uk.co.lophtware.msfreference.tests

import org.scalatest.matchers.{HavePropertyMatcher, HavePropertyMatchResult}
import spinal.core.sim._

import uk.co.lophtware.msfreference.pins.TristatePin

trait TristatePinMatchers {
	def nonTristatedOutValueOf(expectedValue: Boolean) = new HavePropertyMatcher[TristatePin.IoBundle, Boolean] {
		def apply(pin: TristatePin.IoBundle) = HavePropertyMatchResult(
			pin.outValue.toBoolean == expectedValue && !pin.isTristated.toBoolean,
			"outValue (non-tristated)",
			expectedValue,
			pin.outValue.toBoolean
		)
	}

	def tristatedOutValueOf(expectedValue: Boolean) = new HavePropertyMatcher[TristatePin.IoBundle, Boolean] {
		def apply(pin: TristatePin.IoBundle) = HavePropertyMatchResult(
			pin.outValue.toBoolean == expectedValue && pin.isTristated.toBoolean,
			"outValue (tristated)",
			expectedValue,
			pin.outValue.toBoolean
		)
	}
}
