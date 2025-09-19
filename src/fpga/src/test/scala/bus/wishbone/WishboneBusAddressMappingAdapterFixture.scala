package net.restall.ice40riscvsoc.tests.bus.wishbone

import scala.util.Random

import spinal.core._
import spinal.lib.bus.wishbone.WishboneConfig

import net.restall.ice40riscvsoc.bus.wishbone.WishboneBusAddressMappingAdapter
import net.restall.ice40riscvsoc.tests.bus.wishbone.WishboneBusAddressMappingAdapterFixture.DutCreationMethod

class WishboneBusAddressMappingAdapterFixture(masterConfig: WishboneConfig, slaveAddressWidth: BitCount, slaveAddressAddend: Int, dutCreationMethod: DutCreationMethod.Enum) extends Component {
	val io = new WishboneBusAddressMappingAdapter.IoBundle(masterConfig, slaveAddressWidth)
	private val dut = createAndWireDut()

	private def createAndWireDut() = dutCreationMethod match {
		case DutCreationMethod.FactoryTakingMasterAndSlave => createWiredDutViaApplyFactoryTakingMasterAndSlave()
		case DutCreationMethod.FactoryTakingMaster => createWiredDutViaApplyFactoryTakingMaster()
		case DutCreationMethod.FactoryTakingSlave => createWiredDutViaApplyFactoryTakingSlave()
		case _ => createAndWireDutManually()
	}

	private def createWiredDutViaApplyFactoryTakingMasterAndSlave() = WishboneBusAddressMappingAdapter(io.master, io.slave, slaveAddressMapper)

	private def slaveAddressMapper(adr: UInt) = ((adr + slaveAddressAddend) & slaveAddressMask).resize(io.slave.ADR.getWidth)

	def slaveAddressMask = (1 << io.slave.ADR.getWidth) - 1

	private def createWiredDutViaApplyFactoryTakingMaster() = {
		val dut = WishboneBusAddressMappingAdapter(io.master, slaveAddressWidth, slaveAddressMapper)
		io.slave <> dut.io.slave
		dut
	}

	private def createWiredDutViaApplyFactoryTakingSlave() = {
		val dut = WishboneBusAddressMappingAdapter(masterConfig.addressWidth bits, io.slave, slaveAddressMapper)
		io.master <> dut.io.master
		dut
	}

	private def createAndWireDutManually() = {
		val dut = new WishboneBusAddressMappingAdapter(masterConfig, slaveAddressWidth, slaveAddressMapper)
		io <> dut.io
		dut
	}

	def anyMasterAddressValue() = Random.nextInt(1 << io.master.ADR.getWidth)

	def anySel() = Random.nextInt(1 << io.master.SEL.getWidth)

	def anyData() = Random.nextInt(1 << io.master.DAT_MOSI.getWidth)
}

object WishboneBusAddressMappingAdapterFixture {
	object DutCreationMethod extends Enumeration {
		type Enum = Value
		val Constructor, FactoryTakingMasterAndSlave, FactoryTakingSlave, FactoryTakingMaster = Value
	}
}
