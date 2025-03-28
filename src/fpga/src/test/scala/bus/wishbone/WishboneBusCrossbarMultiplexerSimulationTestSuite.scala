package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusDataExpander

class WishboneBusCrossbarMultiplexerSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean): IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory, useOptionalSignals=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory, useOptionalSignals=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean, useOptionalSignals: Boolean) = Array(
		new WishboneBusCrossbarMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=if (useOptionalSignals) 4 else 0,
				useSTALL=useOptionalSignals,
				useLOCK=useOptionalSignals,
				useERR=useOptionalSignals,
				useRTY=useOptionalSignals,
				useCTI=useOptionalSignals,
				tgaWidth=if (useOptionalSignals) 3 else 0,
				tgcWidth=if (useOptionalSignals) 3 else 0,
				tgdWidth=if (useOptionalSignals) 3 else 0,
				useBTE=useOptionalSignals),
			numberOfMasters=1,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory)/*,
		new WishboneBusCrossbarMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=if (useOptionalSignals) 4 else 0,
				useSTALL=useOptionalSignals,
				useLOCK=useOptionalSignals,
				useERR=useOptionalSignals,
				useRTY=useOptionalSignals,
				useCTI=useOptionalSignals,
				tgaWidth=if (useOptionalSignals) 3 else 0,
				tgcWidth=if (useOptionalSignals) 3 else 0,
				tgdWidth=if (useOptionalSignals) 3 else 0,
				useBTE=useOptionalSignals),
			numberOfMasters=1,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusCrossbarMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=if (useOptionalSignals) 4 else 0,
				useSTALL=useOptionalSignals,
				useLOCK=useOptionalSignals,
				useERR=useOptionalSignals,
				useRTY=useOptionalSignals,
				useCTI=useOptionalSignals,
				tgaWidth=if (useOptionalSignals) 3 else 0,
				tgcWidth=if (useOptionalSignals) 3 else 0,
				tgdWidth=if (useOptionalSignals) 3 else 0,
				useBTE=useOptionalSignals),
			numberOfMasters=2,
			numberOfSlaves=1,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusCrossbarMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=if (useOptionalSignals) 4 else 0,
				useSTALL=useOptionalSignals,
				useLOCK=useOptionalSignals,
				useERR=useOptionalSignals,
				useRTY=useOptionalSignals,
				useCTI=useOptionalSignals,
				tgaWidth=if (useOptionalSignals) 3 else 0,
				tgcWidth=if (useOptionalSignals) 3 else 0,
				tgdWidth=if (useOptionalSignals) 3 else 0,
				useBTE=useOptionalSignals),
			numberOfMasters=2,
			numberOfSlaves=2,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusCrossbarMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=if (useOptionalSignals) 4 else 0,
				useSTALL=useOptionalSignals,
				useLOCK=useOptionalSignals,
				useERR=useOptionalSignals,
				useRTY=useOptionalSignals,
				useCTI=useOptionalSignals,
				tgaWidth=if (useOptionalSignals) 3 else 0,
				tgcWidth=if (useOptionalSignals) 3 else 0,
				tgdWidth=if (useOptionalSignals) 3 else 0,
				useBTE=useOptionalSignals),
			numberOfMasters=5,
			numberOfSlaves=8,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory)*/)
}
