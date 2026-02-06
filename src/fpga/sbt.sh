#!/usr/bin/env bash
THIS_DIR=$(dirname "$(readlink -f "${BASH_SOURCE[0]}")");
PATH_HACKS=${THIS_DIR}/freebsd-path-hacks;
which g++ || {
	# SpinalHDL has some hard-coded calls to g++ when invoking the Icarus Verilog back-end :-/
	mkdir -p ${PATH_HACKS};
	cat > ${PATH_HACKS}/g++ <<-EOF
		#!/usr/bin/env bash
		c++ -I/usr/local/include -I/usr/local/openjdk17/include/freebsd \$*;
	EOF
	chmod +x ${PATH_HACKS}/g++;
};

PATH=$PATH:${THIS_DIR}/src/thirdparty/oss-cad-suite/bin:${PATH_HACKS} sbt $*;
