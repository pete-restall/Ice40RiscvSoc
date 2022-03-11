import pytest
from myhdl import *
from src.core.femtorv32_bus import Femtorv32Bus

class Femtorv32BusFixture:
	def __init__(self):
		self._bus = Femtorv32Bus()

	@property
	def bus(self):
		return self._bus

class TestFemtorv32Bus:
	@pytest.fixture(scope='function')
	def fixture(self):
		return Femtorv32BusFixture()

	def test_address_is_modbv_signal(self, fixture):
		expected = Signal(modbv())
		assert isinstance(fixture.bus.address, expected.__class__)
		assert isinstance(fixture.bus.address.val, expected.val.__class__)

	def test_address_is_24bits(self, fixture):
		assert len(fixture.bus.address) == 24

	def test_wdata_is_intbv_signal(self, fixture):
		expected = Signal(intbv())
		assert isinstance(fixture.bus.wdata, expected.__class__)
		assert isinstance(fixture.bus.wdata.val, expected.val.__class__)

	def test_wdata_is_32bits(self, fixture):
		assert len(fixture.bus.wdata) == 32

	def test_wmask_is_intbv_signal(self, fixture):
		expected = Signal(intbv())
		assert isinstance(fixture.bus.wmask, expected.__class__)
		assert isinstance(fixture.bus.wmask.val, expected.val.__class__)

	def test_wmask_is_4bits(self, fixture):
		assert len(fixture.bus.wmask) == 4

	def test_wbusy_is_boolean_signal(self, fixture):
		expected = Signal(bool(0))
		assert isinstance(fixture.bus.wbusy, expected.__class__)
		assert isinstance(fixture.bus.wbusy.val, expected.val.__class__)

	def test_rdata_is_intbv_signal(self, fixture):
		expected = Signal(intbv())
		assert isinstance(fixture.bus.rdata, expected.__class__)
		assert isinstance(fixture.bus.rdata.val, expected.val.__class__)

	def test_rdata_is_32bits(self, fixture):
		assert len(fixture.bus.rdata) == 32

	def test_rstrb_is_boolean_signal(self, fixture):
		expected = Signal(bool(0))
		assert isinstance(fixture.bus.rstrb, expected.__class__)
		assert isinstance(fixture.bus.rstrb.val, expected.val.__class__)

	def test_rbusy_is_boolean_signal(self, fixture):
		expected = Signal(bool(0))
		assert isinstance(fixture.bus.rbusy, expected.__class__)
		assert isinstance(fixture.bus.rbusy.val, expected.val.__class__)
