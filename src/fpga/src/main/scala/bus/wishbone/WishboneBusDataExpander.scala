package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

class WishboneBusDataExpander(slaveConfig: WishboneConfig, numberOfSlaves: Int) extends Component {
	val io = new WishboneBusDataExpander.IoBundle(slaveConfig, numberOfSlaves)
}

object WishboneBusDataExpander {
	case class IoBundle(private val slaveConfig: WishboneConfig, private val numberOfSlaves: Int) extends Bundle {
		if (slaveConfig == null) {
			throw new IllegalArgumentException("Wishbone slave configuration must be specified; arg=slaveConfig, value=null")
		}

		if (numberOfSlaves < 1) {
			throw new IllegalArgumentException(s"Number of Wishbone slaves must be at least 1; arg=numberOfSlaves, value=${numberOfSlaves}")
		}

		val master = new Wishbone(new WishboneConfig(
			addressWidth=slaveConfig.addressWidth,
			dataWidth=numberOfSlaves * slaveConfig.dataWidth,
			selWidth=numberOfSlaves * slaveConfig.selWidth,
			useSTALL=slaveConfig.useSTALL,
			useLOCK=slaveConfig.useLOCK,
			useERR=slaveConfig.useERR,
			useRTY=slaveConfig.useRTY,
			useCTI=slaveConfig.useCTI,
			tgaWidth=slaveConfig.tgaWidth,
			tgcWidth=slaveConfig.tgcWidth,
			tgdWidth=slaveConfig.tgdWidth,
			useBTE=slaveConfig.useBTE))

		val slaves = Array.fill(numberOfSlaves) { spinal.lib.master(new Wishbone(slaveConfig)) }
	}
}
