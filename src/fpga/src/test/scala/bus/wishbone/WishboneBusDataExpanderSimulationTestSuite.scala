package uk.co.lophtware.msfreference.tests.bus.wishbone

import org.scalatest.flatspec._
import org.scalatest.Suite
import spinal.lib.bus.wishbone.WishboneConfig

import uk.co.lophtware.msfreference.bus.wishbone.WishboneBusDataExpander

class WishboneBusDataExpanderSimulationTestSuite extends AnyFlatSpec {
	override def nestedSuites: IndexedSeq[Suite] = Array(
		new WishboneBusDataExpanderSimulationTest(
			slaveConfig=new WishboneConfig(
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
			numberOfSlaves=2),
		new WishboneBusDataExpanderSimulationTest(
			slaveConfig=new WishboneConfig(
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
			numberOfSlaves=2),
		new WishboneBusDataExpanderSimulationTest(
			slaveConfig=new WishboneConfig(
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
			numberOfSlaves=2),
		new WishboneBusDataExpanderSimulationTest(
			slaveConfig=new WishboneConfig(
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
			numberOfSlaves=2))
}
