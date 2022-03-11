from myhdl import *

@block
def TristateOutputDriver(pin, reset, en, val):
	pin_driver = pin.driver()
	is_driven = Signal(False)

	@always_comb
	def is_driven_when_enabled_and_not_reset():
		nonlocal en
		nonlocal reset
		nonlocal is_driven
		is_driven.next = (reset != reset.active and en == en.active)

	@always_comb
	def tristate_or_drive():
		nonlocal is_driven
		nonlocal pin_driver
		pin_driver.next = val if is_driven else None

	return is_driven_when_enabled_and_not_reset, tristate_or_drive
