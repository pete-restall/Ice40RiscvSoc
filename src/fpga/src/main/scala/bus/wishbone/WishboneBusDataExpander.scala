package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

class WishboneBusDataExpander(slaveConfig: WishboneConfig, numberOfSlaves: Int) extends Component {
	val io = new WishboneBusDataExpander.IoBundle(slaveConfig, numberOfSlaves)

	io.master.ACK := areAllHigh(slave => slave.ACK)
	Option(io.master.ERR).map(x => x := isAnyHigh(slave => slave.ERR))
	Option(io.master.STALL).map(x => x := isAnyHigh(slave => slave.STALL))
	Option(io.master.RTY).map(x => x := isAnyHigh(slave => slave.RTY))

	val dataSliceIndices = sliceIndices(numberOfSlaves, slaveConfig.dataWidth)
	val selSliceIndices = sliceIndices(numberOfSlaves, slaveConfig.selWidth)
	for ((slave, (i, j), (m, n)) <- io.slaves.lazyZip(dataSliceIndices).lazyZip(selSliceIndices)) {
		slave.ADR := io.master.ADR
		slave.CYC := io.master.CYC
		slave.STB := io.master.STB
		slave.SEL := io.master.SEL(m downto n)
		slave.WE := io.master.WE
		slave.DAT_MOSI := io.master.DAT_MOSI(i downto j)
		io.master.DAT_MISO(i downto j) := slave.DAT_MISO
	}

	private def areAllHigh(isSet: (Wishbone) => Bool) = io.slaves.foldLeft(True)((acc, slave) => acc && isSet(slave))

	private def isAnyHigh(isSet: (Wishbone) => Bool) = io.slaves.foldLeft(False)((acc, slave) => acc || isSet(slave))

	private def sliceIndices(numberOfSlices: Int, sliceWidth: Int) = {
		val sliceLeftIndices = sliceWidth - 1 until numberOfSlices * sliceWidth by sliceWidth
		val sliceRightIndices = 0 until numberOfSlices * sliceWidth by sliceWidth
		sliceLeftIndices.zip(sliceRightIndices)
	}
	// TODO: PLUS ALL THE WIRING OF THE OTHER LINES NOT LISTED ABOVE (TGA, TGC, TGD_MISO, TGD_MOSI, LOCK, CTI, BTE, ETC.)
}

object WishboneBusDataExpander {
	case class IoBundle(private val slaveConfig: WishboneConfig, private val numberOfSlaves: Int) extends Bundle {
		if (slaveConfig == null) {
			throw new IllegalArgumentException("Wishbone slave configuration must be specified; arg=slaveConfig, value=null")
		}

		if (numberOfSlaves < 1) {
			throw new IllegalArgumentException(s"Number of Wishbone slaves must be at least 1; arg=numberOfSlaves, value=${numberOfSlaves}")
		}

		val master = spinal.lib.slave(new Wishbone(new WishboneConfig(
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
			useBTE=slaveConfig.useBTE)))

		val slaves = Array.fill(numberOfSlaves) { spinal.lib.master(new Wishbone(slaveConfig)) }
	}
}
