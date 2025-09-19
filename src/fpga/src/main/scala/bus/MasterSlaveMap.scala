package net.restall.ice40riscvsoc.bus

import spinal.core._
import spinal.lib.IMasterSlave

import net.restall.ice40riscvsoc.ArgumentPreconditionExtensions._
import net.restall.ice40riscvsoc.ValueBitWidthExtensions._
import net.restall.ice40riscvsoc.multiplexing.SimpleEncoder

class MasterSlaveMap[TBus <: IMasterSlave](val masters: Seq[TBus], val slaves: Seq[TBus]) extends Component {
	masters.mustNotContainNull("masters", "Bus masters must all be specified")
	if (masters.length < 1) {
		throw masters.isOutOfRange("masters", "Sequence of bus masters must not be empty")
	}

	if (masters.distinct.length != masters.length) {
		throw new IllegalArgumentException("Sequence of bus masters must not contain duplicates; arg=masters")
	}

	slaves.mustNotContainNull("slaves", "Bus slaves must all be specified")
	if (slaves.length < 1) {
		throw slaves.isOutOfRange("slaves", "Sequence of bus slaves must not be empty")
	}

	if (slaves.distinct.length != slaves.length) {
		throw new IllegalArgumentException("Sequence of bus slaves must not contain duplicates; arg=slaves")
	}

	val io = new MasterSlaveMap.IoBundle(masters.length, slaves.length)
	noIoPrefix()

	io.masters.foreach { master =>
		val encoder = SimpleEncoder(master.slaveSelects.head, master.slaveSelects.tail.toSeq:_*)
		master.index := encoder.io.output
		master.isValid := encoder.io.isValid
	}
}

object MasterSlaveMap {
	case class IoBundle(private val numberOfMasters: Int, private val numberOfSlaves: Int) extends Bundle {
		if (numberOfMasters < 1) {
			throw numberOfMasters.isOutOfRange("numberOfMasters", "Number of bus masters must be at least 1")
		}

		val masters = Array.fill(numberOfMasters) { new MasterIoBundle(numberOfSlaves) }
	}

	case class MasterIoBundle(private val numberOfSlaves: Int) extends Bundle {
		if (numberOfSlaves < 1) {
			throw numberOfSlaves.isOutOfRange("numberOfSlaves", "Number of bus slaves must be at least 1")
		}

		val index = out UInt(numberOfSlaves.toCombinationalBitWidth)
		val isValid = out Bool()
		val slaveSelects = in Vec(Bool, numberOfSlaves)
	}

	def apply[TBus <: IMasterSlave](firstMapping: (TBus, TBus => Bool, TBus), otherMappings: (TBus, TBus => Bool, TBus)*): MasterSlaveMap[TBus] = {
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
		val map = new MasterSlaveMap[TBus](masters, slaves)
		createMapFor(map, "firstMapping", firstMapping)
		otherMappings.foreach { createMapFor(map, "otherMappings", _) }
		map.io.masters.flatMap(_.slaveSelects).filter(!_.hasAssignement).foreach(_ := False)
		map
	}

	private def errorForNull(part: String) = s"All ${part} in the master-slave mappings must be specified"

	private def createMapFor[TBus <: IMasterSlave](map: MasterSlaveMap[TBus], argName: String, mapping: (TBus, TBus => Bool, TBus)) = {
		val masterIndex = map.masters.indexOf(mapping._1)
		val selector = mapping._2(map.masters(masterIndex))
		selector.mustNotBeNull(argName, "All selectors in the master-slave mappings must return a boolean")
		map.io.masters(masterIndex).slaveSelects(map.slaves.indexOf(mapping._3)) := selector
	}
}
