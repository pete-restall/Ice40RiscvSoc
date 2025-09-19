package net.restall.ice40riscvsoc.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

class WishboneBusSelMappingAdapter(masterConfig: WishboneConfig, slaveSelWidth: BitCount, slaveSelMapper: Bits => Bits) extends Component {
	if (slaveSelMapper == null) {
		throw new IllegalArgumentException("Wishbone slave SEL mapper must be specified; arg=slaveSelMapper, value=null")
	}

	val io = new WishboneBusSelMappingAdapter.IoBundle(masterConfig, slaveSelWidth)
	noIoPrefix()

	if (slaveSelWidth.value > 0) {
		val mappedSlaveSel = slaveSelMapper(io.master.SEL)
		if (mappedSlaveSel == null) {
			throw new IllegalArgumentException("Wishbone slave SEL mapper returned a null when a mapped Bits instance was expected; arg=slaveSelMapper, value=null")
		}

		io.slave.SEL := mappedSlaveSel
	}

	io.master.DAT_MISO := io.slave.DAT_MISO
	io.master.ACK := io.slave.ACK
	Option(io.master.RTY).map(_ := io.slave.RTY)
	Option(io.master.ERR).map(_ := io.slave.ERR)
	Option(io.master.STALL).map(_ := io.slave.STALL)

	io.slave.CYC := io.master.CYC
	io.slave.WE := io.master.WE
	io.slave.ADR := io.master.ADR
	io.slave.STB := io.master.STB
	io.slave.DAT_MOSI := io.master.DAT_MOSI
	Option(io.slave.LOCK).map(_ := io.master.LOCK)

	// TODO: PLUS ALL THE WIRING OF THE OTHER LINES NOT LISTED ABOVE (TGA, TGC, TGD_MISO, TGD_MOSI, CTI, BTE, ETC.)
}

object WishboneBusSelMappingAdapter {
	case class IoBundle(private val masterConfig: WishboneConfig, private val slaveSelWidth: BitCount) extends Bundle {
		if (masterConfig == null) {
			throw new IllegalArgumentException("Wishbone master configuration must be specified; arg=masterConfig, value=null")
		}

		if (slaveSelWidth == null) {
			throw new IllegalArgumentException("Wishbone slave SEL width must be specified; arg=slaveSelWidth, value=null")
		}

		if (slaveSelWidth.value < 0) {
			throw new IllegalArgumentException(s"Wishbone slave SEL width must be zero or more bits; arg=slaveSelWidth, value=${slaveSelWidth}")
		}

		val master = spinal.lib.slave(new Wishbone(masterConfig))
		val slave = spinal.lib.master(new Wishbone(masterConfig.copy(selWidth=slaveSelWidth.value)))
	}

	def apply(master: Wishbone, slave: Wishbone, slaveSelMapper: Bits => Bits): WishboneBusSelMappingAdapter = {
		if (master == null) {
			throw new IllegalArgumentException("Wishbone master must be specified; arg=master, value=null")
		}

		if (slave == null) {
			throw new IllegalArgumentException("Wishbone slave must be specified; arg=slave, value=null")
		}

		if (slaveSelMapper == null) {
			throw new IllegalArgumentException("Wishbone SEL mapper function must be specified; arg=slaveSelMapper, value=null")
		}

		if (master.config != slave.config.copy(selWidth=master.config.selWidth)) {
			throw new IllegalArgumentException("Wishbone master and slave must have the same configuration except possibly the width of SEL; arg=slave")
		}

		val adapter = new WishboneBusSelMappingAdapter(master.config, slave.config.selWidth bits, slaveSelMapper)
		adapter.io.master <> master
		adapter.io.slave <> slave
		adapter
	}

	def apply(masterSelWidth: BitCount, slave: Wishbone, slaveSelMapper: Bits => Bits): WishboneBusSelMappingAdapter = {
		if (masterSelWidth == null) {
			throw new IllegalArgumentException("Width of the Wishbone master's SEL component must be specified; arg=masterSelWidth, value=null")
		}

		if (slave == null) {
			throw new IllegalArgumentException("Wishbone slave must be specified; arg=slave, value=null")
		}

		val adapter = new WishboneBusSelMappingAdapter(slave.config.copy(selWidth=masterSelWidth.value), slave.config.selWidth bits, slaveSelMapper)
		adapter.io.slave <> slave
		adapter
	}

	def apply(master: Wishbone, slaveSelWidth: BitCount, slaveSelMapper: Bits => Bits): WishboneBusSelMappingAdapter = {
		if (master == null) {
			throw new IllegalArgumentException("Wishbone master must be specified; arg=master, value=null")
		}

		if (slaveSelWidth == null) {
			throw new IllegalArgumentException("Width of the Wishbone slave's SEL component must be specified; arg=slaveSelWidth, value=null")
		}

		val adapter = new WishboneBusSelMappingAdapter(master.config, slaveSelWidth, slaveSelMapper)
		adapter.io.master <> master
		adapter
	}
}
