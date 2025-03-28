package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusAddressMappingAdapter
import uk.co.lophtware.msfreference.tests.bus.wishbone.WishboneBusAddressMappingAdapterFixture.DutCreationMethod

class WishboneBusAddressMappingAdapterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreationMethod=DutCreationMethod.Constructor) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingMasterAndSlave) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingMaster) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingSlave)

	private def createTestCasesWith(dutCreationMethod: DutCreationMethod.Enum) = Array(
		new WishboneBusAddressMappingAdapterSimulationTest(
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
			slaveAddressWidth=8 bits,
			slaveAddressAddend=0x1234,
			dutCreationMethod=dutCreationMethod),
		new WishboneBusAddressMappingAdapterSimulationTest(
			masterConfig=new WishboneConfig(
				addressWidth=16,
				dataWidth=16,
				selWidth=0,
				useSTALL=true,
				useLOCK=true,
				useERR=true,
				useRTY=true,
				useCTI=false,
				tgaWidth=0,
				tgcWidth=0,
				tgdWidth=0,
				useBTE=false),
			slaveAddressWidth=32 bits,
			slaveAddressAddend=0x4321,
			dutCreationMethod=dutCreationMethod))
}
