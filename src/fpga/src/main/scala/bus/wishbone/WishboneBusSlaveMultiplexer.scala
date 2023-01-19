package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.ValueBitWidthExtensions._

class WishboneBusSlaveMultiplexer(busConfig: WishboneConfig, numberOfSlaves: Int) extends Component {
	val io = new WishboneBusSlaveMultiplexer.IoBundle(busConfig, numberOfSlaves)
	noIoPrefix()

	io.slaves.zipWithIndex.foreach { case(slave, index) =>
		slave.DAT_MOSI := io.master.DAT_MOSI
		slave.ADR := io.master.ADR
		slave.WE := io.master.WE
		slave.CYC := io.master.CYC && io.selector === index
		slave.STB := io.master.STB && io.selector === index
		Option(slave.SEL).map(_ := io.master.SEL)
	}

	switch(io.selector) {
		(0 until numberOfSlaves).foreach { index =>
			is(index) {
				io.master.DAT_MISO := io.slaves(index).DAT_MISO
				io.master.ACK := io.slaves(index).ACK
				Option(io.master.RTY).map(_ := io.slaves(index).RTY)
				Option(io.master.ERR).map(_ := io.slaves(index).ERR)
				Option(io.master.STALL).map(_ := io.slaves(index).STALL)
			}
		}

		if (numberOfSlaves < io.selector.maxValue + 1) {
			default {
				io.master.DAT_MISO := 0 // TODO: MAKE THIS CONFIGURABLE, THEN (FOR EXAMPLE) A SPECIFIC OPCODE CAN BE EXECUTED ON AN INVALID ADDRESS ACCESS BY A CPU'S INSTRUCTION BUS
				if (io.master.ERR != null) {
					io.master.ERR := io.master.STB
					io.master.ACK := False
				} else {
					io.master.ACK := io.master.STB
				}

				Option(io.master.RTY).map(_ := False)
				Option(io.master.STALL).map(_ := False)
			}
		}
	}

	// TODO: PLUS ALL OF THE OTHER LINES: BTE, LOCK, TGA, TGC, TGD_MISO, TGD_MOSI
}

object WishboneBusSlaveMultiplexer {
	case class IoBundle(private val busConfig: WishboneConfig, private val numberOfSlaves: Int) extends Bundle {
		busConfig.mustNotBeNull("busConfig", "Wishbone bus configuration must be specified")
		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of Wishbone slaves must be at least 1")
		}

		val master = spinal.lib.slave(new Wishbone(busConfig))

		val slaves = Array.fill(numberOfSlaves) { spinal.lib.master(new Wishbone(busConfig)) }

		val selector = in UInt(numberOfSlaves.toCombinationalBitWidth)
	}

	def apply(selector: UInt, firstSlave: Wishbone, otherSlaves: Wishbone*): WishboneBusSlaveMultiplexer = {
		selector.mustNotBeNull("selector", "Wishbone slave selector must be specified; arg=selector, value=null")
		firstSlave.mustNotBeNull("firstSlave", "Wishbone slaves must all be specified; arg=firstSlave, value=null")
		otherSlaves.mustNotContainNull("otherSlaves", "Wishbone slaves must all be specified")

		val indexOfDifferingConfig = otherSlaves.indexWhere(x => x.config != firstSlave.config)
		if (indexOfDifferingConfig >= 0) {
			throw new IllegalArgumentException(s"Wishbone slaves must all have the same configuration; arg=otherSlaves, index=${indexOfDifferingConfig}")
		}

		val mux = new WishboneBusSlaveMultiplexer(firstSlave.config, 1 + otherSlaves.length)
		if (selector.getWidth != mux.io.selector.getWidth) {
			throw new IllegalArgumentException(
				"Selector is too narrow or too wide to properly index all of the Wishbone slaves; arg=selector, " +
				s"selectorWidth=${selector.getWidth bits}, numberOfSlaves=${1 + otherSlaves.length}, expectedWidth=${mux.io.selector.getWidth bits}")
		}

		mux.io.selector := selector
		(firstSlave +: otherSlaves).zip(mux.io.slaves).foreach { case (slave, muxSlave) => slave <> muxSlave }

		mux
	}
}
