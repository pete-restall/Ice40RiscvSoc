package uk.co.lophtware.msfreference.tests.vendor.lattice.ice40

import org.scalatest.flatspec._
import org.scalatest.Suite

class Ice40Ebram4kTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new Ice40Ebram4kAs256x16Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAs512x8Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAs1024x4Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAs2048x2Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAsRead2Write16Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAsRead4Write16Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAsRead8Write16Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new Ice40Ebram4kAsRead16Write8Test(dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
