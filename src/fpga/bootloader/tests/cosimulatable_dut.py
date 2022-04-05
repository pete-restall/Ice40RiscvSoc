import os
from tempfile import mkdtemp
from myhdl import *

_timescale_ps=1 # Must match the Lattice libraries for accurate timings - the simulator takes the highest precision.  Although there's still something weird about the LXT files...

class CosimulatableDut:
	def __init__(self, dut, initial_values=True, extra_signals={}):
		self.cosim_path = mkdtemp()
		this_path = os.path.dirname(os.path.realpath(__file__))
		package_path = this_path + "/.."
		root_path = package_path + "/.."

		verilog_timescale = str(_timescale_ps) + "ps/" + str(_timescale_ps) + "ps"

		dut.convert(
			hdl="Verilog",
			name="dut",
			directory=self.cosim_path,
			testbench=True,
			initial_values=initial_values,
			timescale=verilog_timescale)

		os.system(
			"sed -i 's#module tb_dut;#" +
			"`timescale " + verilog_timescale + "\\n" +
			"module tb_dut;\\n" +
			"initial begin\\n" +
			" $dumpfile(\"" + self.cosim_path + "/dut.lxt\");\\n" +
			" $dumpvars(0, dut);\\n" +
			"end\\n" +
			"#g' " + self.cosim_path + "/tb_dut.v")

		hdl_lib = package_path + "/src/hdl"
		lattice_lib = root_path + "/thirdparty/lattice"
		iverilog_cmd = ("iverilog " +
			"-g 2001 " +
			"-DBENCH " +
			"-y " + lattice_lib + "/ " +
			"-y " + hdl_lib + "/femtorv32/ " +
			"-o " + self.cosim_path + "/dut.o " +
			self.cosim_path + "/dut.v " +
			self.cosim_path + "/tb_dut.v")

		print("iverilog_cmd =", iverilog_cmd)
		os.system(iverilog_cmd)

		cosim_cmd = ("vvp " +
			"-n -v -m " + root_path + "/build/icarus/myhdl.vpi " +
			self.cosim_path + "/dut.o " +
			"-lxt2")

		print("cosim_cmd =", cosim_cmd)
		signals = {**dut.argdict, **extra_signals}
		print("signals =", signals)
		self.dut = Cosimulation(cosim_cmd, **signals)

	def run(self, generators, **kwargs):
		sim = Simulation(generators)
		completed = sim.run(**kwargs)
		if completed != 0:
			sim.quit()

		assert completed == 0

def ps(t):
	return int(t / _timescale_ps)

def ns(t):
	return ps(t * 1e3)

def us(t):
	return ns(t * 1e3)

def ms(t):
	return us(t * 1e3)
