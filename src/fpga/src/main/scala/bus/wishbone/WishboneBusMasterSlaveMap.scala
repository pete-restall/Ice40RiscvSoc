package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.bus.MasterSlaveMap

object WishboneBusMasterSlaveMap {
	def apply(firstMapping: (Wishbone, Wishbone => Bool, Wishbone), otherMappings: (Wishbone, Wishbone => Bool, Wishbone)*): MasterSlaveMap[Wishbone] = {
		val busMap = MasterSlaveMap(firstMapping, otherMappings:_*)

		val masterConfigs = busMap.masters.map(_.config).distinct
		if (masterConfigs.length != 1) {
			throw new IllegalArgumentException("All Wishbone masters must have identical bus configuration; arg=busMap.masters")
		}

		val slaveConfigs = busMap.slaves.map(_.config).distinct
		if (slaveConfigs.length != 1) {
			throw new IllegalArgumentException("All Wishbone slaves must have identical bus configuration; arg=busMap.slaves")
		}

		if (masterConfigs.head != slaveConfigs.head) {
			throw new IllegalArgumentException("All Wishbone slaves must have identical bus configuration to the masters; arg=busMap.slaves")
		}

		busMap
	}
}
