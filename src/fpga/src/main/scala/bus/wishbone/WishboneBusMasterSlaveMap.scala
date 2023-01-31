package uk.co.lophtware.msfreference.bus.wishbone

import spinal.core._
import spinal.lib.bus.wishbone.Wishbone

import uk.co.lophtware.msfreference.ArgumentPreconditionExtensions._
import uk.co.lophtware.msfreference.ValueBitWidthExtensions._
import uk.co.lophtware.msfreference.multiplexing.SimpleEncoder

// TODO: MOST OF THIS CAN BE MADE INTO A GENERIC bus.MasterSlaveMap[TBus] - IT'S ONLY THE SAME-CONFIGURATION TESTS THAT ARE WISHBONE-SPECIFIC
class WishboneBusMasterSlaveMap(val masters: Seq[Wishbone], val slaves: Seq[Wishbone]) extends Component {
	masters.mustNotContainNull("masters", "Wishbone masters must all be specified")
	if (masters.length < 1) {
		throw masters.isOutOfRange("masters", "Sequence of Wishbone masters must not be empty")
	}

	if (masters.distinct.length != masters.length) {
		throw new IllegalArgumentException("Sequence of Wishbone masters must not contain duplicates; arg=masters")
	}

	private val masterConfigs = masters.map(_.config).distinct
	if (masterConfigs.length != 1) {
		throw new IllegalArgumentException("All Wishbone masters must have identical bus configuration; arg=masters")
	}

	slaves.mustNotContainNull("slaves", "Wishbone slaves must all be specified")
	if (slaves.length < 1) {
		throw slaves.isOutOfRange("slaves", "Sequence of Wishbone slaves must not be empty")
	}

	if (slaves.distinct.length != slaves.length) {
		throw new IllegalArgumentException("Sequence of Wishbone slaves must not contain duplicates; arg=slaves")
	}

	private val slaveConfigs = slaves.map(_.config).distinct
	if (slaveConfigs.length != 1) {
		throw new IllegalArgumentException("All Wishbone slaves must have identical bus configuration; arg=slaves")
	}

	if (masterConfigs.head != slaveConfigs.head) {
		throw new IllegalArgumentException("All Wishbone slaves must have identical bus configuration to the masters; arg=slaves")
	}

	val io = new WishboneBusMasterSlaveMap.IoBundle(masters.length, slaves.length)
	noIoPrefix()

	io.masters.foreach { master =>
		val encoder = SimpleEncoder(master.slaveSelects.head, master.slaveSelects.tail.toSeq:_*)
		master.index := encoder.io.output
		master.isValid := encoder.io.isValid
	}
}

object WishboneBusMasterSlaveMap {
	case class IoBundle(private val numberOfMasters: Int, private val numberOfSlaves: Int) extends Bundle {
		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of Wishbone masters must be at least 1")
		}

		val masters = Array.fill(numberOfMasters) { new MasterIoBundle(numberOfSlaves) }
	}

	case class MasterIoBundle(private val numberOfSlaves: Int) extends Bundle {
		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of Wishbone slaves must be at least 1")
		}

		val index = out UInt(numberOfSlaves.toCombinationalBitWidth)
		val isValid = out Bool()
		val slaveSelects = in Vec(Bool, numberOfSlaves)
	}

	def apply(firstMapping: (Wishbone, Wishbone => Bool, Wishbone), otherMappings: (Wishbone, Wishbone => Bool, Wishbone)*): WishboneBusMasterSlaveMap = {
		firstMapping.mustNotBeNull("firstMapping", "At least 1 master-slave mapping must be specified")
		otherMappings.mustNotContainNull("otherMappings", "All master-slave mappings must be specified")

		firstMapping._1.mustNotBeNull("firstMapping", errorForNull("masters"))
		firstMapping._2.mustNotBeNull("firstMapping", errorForNull("selectors"))
		firstMapping._3.mustNotBeNull("firstMapping", errorForNull("slaves"))
		otherMappings.map(_._1).mustNotContainNull("otherMappings", errorForNull("masters"))
		otherMappings.map(_._2).mustNotContainNull("otherMappings", errorForNull("selectors"))
		otherMappings.map(_._3).mustNotContainNull("otherMappings", errorForNull("slaves"))

		val masters = (firstMapping._1 +: otherMappings.map(_._1)).distinct
		val slaves = (firstMapping._3 +: otherMappings.map(_._3)).distinct
		val map = new WishboneBusMasterSlaveMap(masters, slaves)
		createMapFor(map, "firstMapping", firstMapping)
		otherMappings.foreach { createMapFor(map, "otherMappings", _) }
		map.io.masters.flatMap(_.slaveSelects).filter(!_.hasAssignement).foreach(_ := False)
		map
	}

	private def errorForNull(part: String) = s"All ${part} in the master-slave mappings must be specified"

	private def createMapFor(map: WishboneBusMasterSlaveMap, argName: String, mapping: (Wishbone, Wishbone => Bool, Wishbone)) = {
		val masterIndex = map.masters.indexOf(mapping._1)
		val selector = mapping._2(map.masters(masterIndex))
		selector.mustNotBeNull(argName, "All selectors in the master-slave mappings must return a boolean")
		map.io.masters(masterIndex).slaveSelects(map.slaves.indexOf(mapping._3)) := selector
	}
}
