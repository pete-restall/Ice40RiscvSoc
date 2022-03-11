from myhdl import *
from tests.cosimulatable_dut import ps

@block
def ClockDriver(clk, hz):
	period_ps = 1e12 / hz
	half_period = ps(period_ps * 0.5)

	@instance
	def driver():
		nonlocal clk
		nonlocal half_period
		while True:
			clk.next = not clk
			yield delay(half_period)

	return driver
