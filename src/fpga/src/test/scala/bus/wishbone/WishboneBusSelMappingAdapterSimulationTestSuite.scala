package net.restall.ice40riscvsoc.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusSelMappingAdapter
import net.restall.ice40riscvsoc.tests.bus.wishbone.WishboneBusSelMappingAdapterFixture.DutCreationMethod

class WishboneBusSelMappingAdapterSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] =
		createTestCasesWith(dutCreationMethod=DutCreationMethod.Constructor) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingMasterAndSlave) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingMaster) ++
		createTestCasesWith(dutCreationMethod=DutCreationMethod.FactoryTakingSlave)

	private def createTestCasesWith(dutCreationMethod: DutCreationMethod.Enum) = Array(
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
			dutCreationMethod=dutCreationMethod),
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
			dutCreationMethod=dutCreationMethod))
}
