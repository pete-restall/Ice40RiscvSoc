package uk.co.lophtware.msfreference.tests.formal

import spinal.core._
import spinal.idslplugin._

abstract trait FormalAssertions {
	protected def formallyAssume(condition: Bool)(implicit loc: Location) = assume(condition)(loc)

	protected def formallyAssumeInitial(condition: Bool)(implicit loc: Location) = assumeInitial(condition)(loc)

	protected def formallyAssert(condition: Bool)(implicit loc: Location) = assert(condition)(loc)
}
