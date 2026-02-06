package net.restall.ice40riscvsoc.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._

class WishboneBusDataExpander(slaveConfig: WishboneConfig, numberOfSlaves: Int) extends Component {
	val io = new WishboneBusDataExpander.IoBundle(slaveConfig, numberOfSlaves)
	noIoPrefix()

	io.master.ACK := areAllHigh(slave => slave.ACK)
	Option(io.master.ERR).map(_ := isAnyHigh(slave => slave.ERR))
	Option(io.master.STALL).map(_ := isAnyHigh(slave => slave.STALL))
	Option(io.master.RTY).map(_ := isAnyHigh(slave => slave.RTY))

	private val dataSliceIndices = sliceIndices(numberOfSlaves, slaveConfig.dataWidth)
	private val selSliceIndices = sliceIndices(numberOfSlaves, slaveConfig.selWidth)
	for ((slave, (i, j), (m, n)) <- io.slaves.lazyZip(dataSliceIndices).lazyZip(selSliceIndices)) {
		slave.ADR := io.master.ADR
		slave.CYC := io.master.CYC
		slave.STB := io.master.STB
		Option(slave.SEL).map(_ := io.master.SEL(m downto n))
		slave.WE := io.master.WE
		slave.DAT_MOSI := io.master.DAT_MOSI(i downto j)
		io.master.DAT_MISO(i downto j) := slave.DAT_MISO
	}

	private def areAllHigh(isSet: (Wishbone) => Bool) = io.slaves.foldLeft(True)((acc, slave) => acc && isSet(slave))

	private def isAnyHigh(isSet: (Wishbone) => Bool) = io.slaves.foldLeft(False)((acc, slave) => acc || isSet(slave))

	private def sliceIndices(numberOfSlices: Int, sliceWidth: Int) = {
		lazy val invalidIndices = Array.fill(numberOfSlices)(-1)
		val sliceLeftIndices: Iterable[Int] = sliceIndexRangeFor(sliceWidth - 1, numberOfSlices, sliceWidth).getOrElse(invalidIndices)
		val sliceRightIndices: Iterable[Int] = sliceIndexRangeFor(0, numberOfSlices, sliceWidth).getOrElse(invalidIndices)
		sliceLeftIndices.zip(sliceRightIndices)
	}

	private def sliceIndexRangeFor(start: Int, numberOfSlices: Int, sliceWidth: Int) = if (sliceWidth > 0) {
		Some(start until numberOfSlices * sliceWidth by sliceWidth)
	} else None

	// TODO: PLUS ALL THE WIRING OF THE OTHER LINES NOT LISTED ABOVE (TGA, TGC, TGD_MISO, TGD_MOSI, LOCK, CTI, BTE, ETC.)
}

object WishboneBusDataExpander {
	case class IoBundle(private val slaveConfig: WishboneConfig, private val numberOfSlaves: Int) extends Bundle {
		slaveConfig.mustNotBeNull("slaveConfig", "Wishbone slave configuration must be specified")
		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of Wishbone slaves must be at least 1")
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

	def apply(firstSlave: Wishbone, otherSlaves: Wishbone*): WishboneBusDataExpander = {
		firstSlave.mustNotBeNull("firstSlave", "Wishbone slaves must all be specified")
		otherSlaves.mustNotContainNull("otherSlaves", "Wishbone slaves must all be specified")

		val indexOfDifferingConfig = otherSlaves.indexWhere(x => x.config != firstSlave.config)
		if (indexOfDifferingConfig >= 0) {
			throw new IllegalArgumentException(s"Wishbone slaves must all have the same configuration; arg=otherSlaves, index=${indexOfDifferingConfig}")
		}

		val expander = new WishboneBusDataExpander(firstSlave.config, 1 + otherSlaves.length)
		expander.io.slaves.head <> firstSlave
		expander.io.slaves.tail.zip(otherSlaves).foreach { case(x, y) => x <> y }
		expander
	}
}
