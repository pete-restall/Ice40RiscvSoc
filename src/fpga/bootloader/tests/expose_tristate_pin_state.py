from myhdl import *

class TristatePinStateMonitor:
	def __init__(self):
		self.is_low = Signal(False)
		self.is_high = Signal(False)
		self.is_tristate = Signal(False)
		self.is_invalid = Signal(False)

@block
def ExposeTristatePinState(pin, pin_state):
	@block
	def expose_pin_state(pin, pin_is_low, pin_is_high, pin_is_tristate, pin_is_invalid):
		pin.read = True
		pin_is_low.driven = "reg"
		pin_is_high.driven = "reg"
		pin_is_tristate.driven = "reg"
		pin_is_invalid.driven = "reg"

		@always(pin)
		def unused():
			pass

		return unused

	expose_pin_state.verilog_code = \
"""
always @($pin) begin
	case ($pin)
		1'b0: begin
			$pin_is_low <= 1;
			$pin_is_high <= 0;
			$pin_is_tristate <= 0;
			$pin_is_invalid <= 0;
		end

		1'b1: begin
			$pin_is_low <= 0;
			$pin_is_high <= 1;
			$pin_is_tristate <= 0;
			$pin_is_invalid <= 0;
		end

		1'bz: begin
			$pin_is_low <= 0;
			$pin_is_high <= 0;
			$pin_is_tristate <= 1;
			$pin_is_invalid <= 0;
		end

		default: begin
			$pin_is_low <= 0;
			$pin_is_high <= 0;
			$pin_is_tristate <= 0;
			$pin_is_invalid <= 1;
		end
	endcase
end
"""

	return expose_pin_state(
		pin,
		pin_state.is_low,
		pin_state.is_high,
		pin_state.is_tristate,
		pin_state.is_invalid)
