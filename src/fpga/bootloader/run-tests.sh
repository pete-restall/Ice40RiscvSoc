#!/bin/bash
set -e;
THISDIR="$(dirname "$(readlink -f "$0")")";
RUN_TESTS="pytest";
VENV_ACTIVATE="${THISDIR}/../.venv/bin/activate";

if [ ! -f "${VENV_ACTIVATE}" ]; then
	echo "Ensure there is a .venv; looking for ${VENV_ACTIVATE}";
	exit 1;
fi

. "${VENV_ACTIVATE}";

if [ $# -gt 0 ]; then
	${RUN_TESTS} $*;
else
	# Icarus Verilog and MyHDL have some sort of memory leak that prevents
	# running the entire suite in a single process, so iterate over and run
	# each test class individually:

	for f in `find ${THISDIR} -iname "*_test.py"`; do
		$0 ${f};
		if [ $? -ne 0 ]; then
			echo "Test run terminated after first failure";
			exit $?;
		fi
	done;
fi;
