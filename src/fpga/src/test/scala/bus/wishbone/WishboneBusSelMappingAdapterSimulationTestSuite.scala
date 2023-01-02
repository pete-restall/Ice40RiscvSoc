package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusSelMappingAdapter

class WishboneBusSelMappingAdapterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreatedViaApplyFactory=false) ++
		createTestCasesWith(dutCreatedViaApplyFactory=true)

	private def createTestCasesWith(dutCreatedViaApplyFactory: Boolean) = Array(
		new WishboneBusSelMappingAdapterSimulationTest(
			masterConfig=new WishboneConfig(
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
			slaveSelWidth=16 bits,
			slaveSelAddend=0x1234,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory),
		new WishboneBusSelMappingAdapterSimulationTest(
			masterConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=4,
				useSTALL=true,
				useLOCK=true,
				useERR=true,
				useRTY=true,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			slaveSelWidth=16 bits,
			slaveSelAddend=0x4321,
			dutCreatedViaApplyFactory=dutCreatedViaApplyFactory))
}
