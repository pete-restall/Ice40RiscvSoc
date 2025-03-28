package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSlaveMultiplexer

class WishboneBusSlaveMultiplexerSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean): IndexedSeq[WishboneBusSlaveMultiplexerSimulationTest] = IndexedSeq(
		createTestCasesWith(numberOfSlaves=1, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfSlaves=2, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfSlaves=3, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfSlaves=4, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		createTestCasesWith(numberOfSlaves=6, dutCreatedViaApplyFactory=dutCreatedViaApplyFactory)).flatten

	private def createTestCasesWith(numberOfSlaves: Int, dutCreatedViaApplyFactory: Boolean) = Array(
		new WishboneBusSlaveMultiplexerSimulationTest(
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
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSlaveMultiplexerSimulationTest(
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
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSlaveMultiplexerSimulationTest(
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
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSlaveMultiplexerSimulationTest(
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
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSlaveMultiplexerSimulationTest(
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
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSlaveMultiplexerSimulationTest(
			busConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=true,
				useLOCK=true,
				useERR=true,
				useRTY=true,
				useCTI=true,
				tgaWidth=5,
				tgcWidth=6,
				tgdWidth=7,
				useBTE=true),
			numberOfSlaves=numberOfSlaves,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
