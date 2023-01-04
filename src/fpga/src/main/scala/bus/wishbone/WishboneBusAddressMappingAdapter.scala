package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

class WishboneBusAddressMappingAdapter(masterConfig: WishboneConfig, slaveAddressWidth: BitCount, slaveAddressMapper: UInt => UInt) extends Component {
	if (slaveAddressMapper == null) {
		throw new IllegalArgumentException("Wishbone slave ADR mapper must be specified; arg=slaveAddressMapper, value=null")
	}

	val io = new WishboneBusAddressMappingAdapter.IoBundle(masterConfig, slaveAddressWidth)
	noIoPrefix()

	if (slaveAddressWidth.value > 0) {
		val mappedSlaveAddress = slaveAddressMapper(io.master.ADR)
		if (mappedSlaveAddress == null) {
			throw new IllegalArgumentException("Wishbone slave ADR mapper returned a null when a mapped UInt instance was expected; arg=slaveAddressMapper, value=null")
		}

		io.slave.ADR := mappedSlaveAddress
	} else {
		io.slave.ADR := U(0, 0 bits)
	}

	io.master.DAT_MISO := io.slave.DAT_MISO
	io.master.ACK := io.slave.ACK
	Option(io.master.RTY).map(_ := io.slave.RTY)
	Option(io.master.ERR).map(_ := io.slave.ERR)
	Option(io.master.STALL).map(_ := io.slave.STALL)

	io.slave.CYC := io.master.CYC
	io.slave.WE := io.master.WE
	io.slave.STB := io.master.STB
	io.slave.DAT_MOSI := io.master.DAT_MOSI
	Option(io.slave.SEL).map(_ := io.master.SEL)
	Option(io.slave.LOCK).map(_ := io.master.LOCK)

	// TODO: PLUS ALL THE WIRING OF THE OTHER LINES NOT LISTED ABOVE (TGA, TGC, TGD_MISO, TGD_MOSI, CTI, BTE, ETC.)
}

object WishboneBusAddressMappingAdapter {
	case class IoBundle(private val masterConfig: WishboneConfig, private val slaveAddressWidth: BitCount) extends Bundle {
		if (masterConfig == null) {
			throw new IllegalArgumentException("Wishbone master configuration must be specified; arg=masterConfig, value=null")
		}

		if (slaveAddressWidth == null) {
			throw new IllegalArgumentException("Wishbone slave ADR width must be specified; arg=slaveAddressWidth, value=null")
		}

		if (slaveAddressWidth.value < 0) {
			throw new IllegalArgumentException(s"Wishbone slave ADR width must be zero or more bits; arg=slaveAddressWidth, value=${slaveAddressWidth}")
		}

		val master = spinal.lib.slave(new Wishbone(masterConfig))
		val slave = spinal.lib.master(new Wishbone(masterConfig.copy(addressWidth=slaveAddressWidth.value)))
	}

	def apply(master: Wishbone, slave: Wishbone, slaveAddressMapper: UInt => UInt): WishboneBusAddressMappingAdapter = {
		if (master == null) {
			throw new IllegalArgumentException("Wishbone master must be specified; arg=master, value=null")
		}

		if (slave == null) {
			throw new IllegalArgumentException("Wishbone slave must be specified; arg=slave, value=null")
		}

		if (slaveAddressMapper == null) {
			throw new IllegalArgumentException("Wishbone ADR mapper function must be specified; arg=slaveAddressMapper, value=null")
		}

		if (master.config != slave.config.copy(addressWidth=master.config.addressWidth)) {
			throw new IllegalArgumentException("Wishbone master and slave must have the same configuration except possibly the width of ADR; arg=slave")
		}

		val adapter = new WishboneBusAddressMappingAdapter(master.config, slave.config.addressWidth bits, slaveAddressMapper)
		adapter.io.master <> master
		adapter.io.slave <> slave
		adapter
	}

	def apply(masterAddressWidth: BitCount, slave: Wishbone, slaveAddressMapper: UInt => UInt): WishboneBusAddressMappingAdapter = {
		if (masterAddressWidth == null) {
			throw new IllegalArgumentException("Width of the Wishbone master's ADR component must be specified; arg=masterAddressWidth, value=null")
		}

		if (slave == null) {
			throw new IllegalArgumentException("Wishbone slave must be specified; arg=slave, value=null")
		}

		val adapter = new WishboneBusAddressMappingAdapter(slave.config.copy(addressWidth=masterAddressWidth.value), slave.config.addressWidth bits, slaveAddressMapper)
		adapter.io.slave <> slave
		adapter
	}

	def apply(master: Wishbone, slaveAddressWidth: BitCount, slaveAddressMapper: UInt => UInt): WishboneBusAddressMappingAdapter = {
		if (master == null) {
			throw new IllegalArgumentException("Wishbone master must be specified; arg=master, value=null")
		}

		if (slaveAddressWidth == null) {
			throw new IllegalArgumentException("Width of the Wishbone slave's ADR component must be specified; arg=slaveAddressWidth, value=null")
		}

		val adapter = new WishboneBusAddressMappingAdapter(master.config, slaveAddressWidth, slaveAddressMapper)
		adapter.io.master <> master
		adapter
	}
}
