package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.ValueBitWidthExtensions._

class WishboneBusMasterMultiplexer(busConfig: WishboneConfig, numberOfMasters: Int) extends Component {
	val io = new WishboneBusMasterMultiplexer.IoBundle(busConfig, numberOfMasters)
	noIoPrefix()

	io.masters.zipWithIndex.foreach { case(master, index) =>
		master.DAT_MISO := io.slave.DAT_MISO
		master.ACK := io.slave.ACK && io.selector === index
		Option(master.RTY).map(_ := io.slave.RTY && io.selector === index)
		Option(master.ERR).map(_ := io.slave.ERR && io.selector === index)
		Option(master.STALL).map(_ := io.slave.STALL && io.selector === index)
		Option(master.TGD_MISO).map(_ := io.slave.TGD_MISO)
	}

	switch(io.selector) {
		(0 until numberOfMasters).foreach { index =>
			is(index) {
				io.slave.DAT_MOSI := io.masters(index).DAT_MOSI
				io.slave.ADR := io.masters(index).ADR
				io.slave.WE := io.masters(index).WE
				io.slave.CYC := io.masters(index).CYC
				io.slave.STB := io.masters(index).STB
				Option(io.slave.SEL).map(_ := io.masters(index).SEL)
				Option(io.slave.LOCK).map(_ := io.masters(index).LOCK)
				Option(io.slave.BTE).map(_ := io.masters(index).BTE)
				Option(io.slave.CTI).map(_ := io.masters(index).CTI)
				Option(io.slave.TGA).map(_ := io.masters(index).TGA)
				Option(io.slave.TGC).map(_ := io.masters(index).TGC)
				Option(io.slave.TGD_MOSI).map(_ := io.masters(index).TGD_MOSI)
			}
		}

		if (numberOfMasters < io.selector.maxValue + 1) {
			default {
				io.slave.DAT_MOSI := 0
				io.slave.ADR := 0
				io.slave.WE := False
				io.slave.CYC := False
				io.slave.STB := False
				Option(io.slave.SEL).map(_ := 0)
				Option(io.slave.LOCK).map(_ := False)
				Option(io.slave.BTE).map(_ := 0)
				Option(io.slave.CTI).map(_ := 0)
				Option(io.slave.TGA).map(_ := 0)
				Option(io.slave.TGC).map(_ := 0)
				Option(io.slave.TGD_MOSI).map(_ := 0)
			}
		}
	}
}

object WishboneBusMasterMultiplexer {
	case class IoBundle(private val busConfig: WishboneConfig, private val numberOfMasters: Int) extends Bundle {
		busConfig.mustNotBeNull("busConfig", "Wishbone bus configuration must be specified")
		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of Wishbone masters must be at least 1")
		}

		val masters = Array.fill(numberOfMasters) { spinal.lib.slave(new Wishbone(busConfig)) }

		val slave = spinal.lib.master(new Wishbone(busConfig))

		val selector = in UInt(numberOfMasters.toCombinationalBitWidth)
	}

	def apply(selector: UInt, firstMaster: Wishbone, otherMasters: Wishbone*): WishboneBusMasterMultiplexer = {
		selector.mustNotBeNull("selector", "Wishbone master selector must be specified; arg=selector, value=null")
		firstMaster.mustNotBeNull("firstMaster", "Wishbone masters must all be specified; arg=firstMaster, value=null")
		otherMasters.mustNotContainNull("otherMasters", "Wishbone masters must all be specified")

		val indexOfDifferingConfig = otherMasters.indexWhere(x => x.config != firstMaster.config)
		if (indexOfDifferingConfig >= 0) {
			throw new IllegalArgumentException(s"Wishbone masters must all have the same configuration; arg=otherMasters, index=${indexOfDifferingConfig}")
		}

		val multiplexer = new WishboneBusMasterMultiplexer(firstMaster.config, 1 + otherMasters.length)
		if (selector.getWidth != multiplexer.io.selector.getWidth) {
			throw new IllegalArgumentException(
				"Selector is too narrow or too wide to properly index all of the Wishbone masters; arg=selector, " +
				s"selectorWidth=${selector.getWidth bits}, numberOfMasters=${1 + otherMasters.length}, expectedWidth=${multiplexer.io.selector.getWidth bits}")
		}

		multiplexer.io.selector := selector
		(firstMaster +: otherMasters).zip(multiplexer.io.masters).foreach { case (master, muxMaster) => master <> muxMaster }

		multiplexer
	}
}
