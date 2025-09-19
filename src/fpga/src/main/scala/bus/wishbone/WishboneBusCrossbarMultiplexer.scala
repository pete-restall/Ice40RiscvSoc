package net.restall.ice40riscvsoc.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.{Wishbone, WishboneConfig}

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.IHaveIo
import net.restall.ice40riscvsoc.ValueBitWidthExtensions._
import net.restall.ice40riscvsoc.bus.{CrossbarArbiter, MasterSlaveMap}

class WishboneBusCrossbarMultiplexer(busConfig: WishboneConfig, numberOfMasters: Int, numberOfSlaves: Int) extends Component {
	val io = new WishboneBusCrossbarMultiplexer.IoBundle(busConfig, numberOfMasters, numberOfSlaves)
	noIoPrefix()

	private val masterMuxes = io.slaves.map { slave =>
		val masters = io.masters.map(master => new Wishbone(busConfig))
		val mux = WishboneBusMasterMultiplexer(slave.grantedMasterIndex, masters.head, masters.tail:_*)
		mux.io.slave >> slave.wishbone
		(mux, masters)
	}

	io.masters.zipWithIndex.foreach { case (master, masterIndex) =>
		val masters = masterMuxes.map(_._2(masterIndex))
		val mux = WishboneBusSlaveMultiplexer(master.requestedSlaveIndex, masters.head, masters.tail:_*)
		mux.io.master << master.wishbone
	}
}

object WishboneBusCrossbarMultiplexer {
	case class IoBundle(private val busConfig: WishboneConfig, private val numberOfMasters: Int, private val numberOfSlaves: Int) extends Bundle {
		busConfig.mustNotBeNull("busConfig", "Wishbone bus configuration must be specified")

		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of Wishbone masters must be at least 1")
		}

		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of Wishbone slaves must be at least 1")
		}

		val masters = Seq.fill(numberOfMasters) { new Bundle {
			val requestedSlaveIndex = in UInt(numberOfSlaves.toCombinationalBitWidth)
			val wishbone = spinal.lib.slave(new Wishbone(busConfig))
		}}

		val slaves = Seq.fill(numberOfSlaves) { new Bundle {
			val grantedMasterIndex = in UInt(numberOfMasters.toCombinationalBitWidth)
			val wishbone = spinal.lib.master(new Wishbone(busConfig))
		}}
	}

	def apply(busMap: MasterSlaveMap[Wishbone], arbiter: IHaveIo[CrossbarArbiter.IoBundle]): WishboneBusCrossbarMultiplexer = {
		busMap.mustNotBeNull("busMap", "Wishbone Bus Master-to-Slave Mappings must be specified")
		arbiter.mustNotBeNull("arbiter", "Crossbar Arbiter must be specified")

		val multiplexer = new WishboneBusCrossbarMultiplexer(busMap.masters.head.config, busMap.masters.length, busMap.slaves.length)

		multiplexer.io.masters.zip(busMap.io.masters).foreach { case (muxMaster, bmMaster) =>
			muxMaster.requestedSlaveIndex := bmMaster.index
		}
		multiplexer.io.masters.zip(busMap.masters).foreach { case (ioMaster, bmMaster) => bmMaster >> ioMaster.wishbone }

		multiplexer.io.slaves.zip(arbiter.io.slaves).foreach { case (muxSlave, arbSlave) =>
			muxSlave.grantedMasterIndex := arbSlave.grantedMasterIndex
		}
		multiplexer.io.slaves.zip(busMap.slaves).foreach { case (ioSlave, bmSlave) => ioSlave.wishbone >> bmSlave }

		multiplexer
	}
}
