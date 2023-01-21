package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusMasterMultiplexer

class WishboneBusMasterMultiplexerSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean): IndexedSeq[WishboneBusMasterMultiplexerSimulationTest] = IndexedSeq(
		createTestCasesWith(numberOfMasters=1, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfMasters=2, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfMasters=3, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfMasters=4, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfMasters=6, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory)).flatten

	private def createTestCasesWith(numberOfMasters: Int, dutCreatedViaApplyFactory: Boolean) = Array(
		new WishboneBusMasterMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=0,
				useSTALL=false,
				useLOCK=false,
				useERR=false,
				useRTY=false,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			numberOfMasters=numberOfMasters,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=false,
				useLOCK=false,
				useERR=false,
				useRTY=false,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			numberOfMasters=numberOfMasters,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=false,
				useLOCK=false,
				useERR=true,
				useRTY=false,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			numberOfMasters=numberOfMasters,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=false,
				useLOCK=false,
				useERR=false,
				useRTY=true,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			numberOfMasters=numberOfMasters,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusMasterMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=true,
				useLOCK=false,
				useERR=false,
				useRTY=false,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			numberOfMasters=numberOfMasters,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
