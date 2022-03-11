#!/bin/bash
set -e;
THISDIR="$(dirname "$(readlink -f "$0")")";
MYHDL_TOVERILOG_PATH="${THISDIR}/.venv/lib64/python3.*/site-packages/myhdl/conversion/_toVerilog.py";
MYHDL_TOVERILOG_RESOLVED_PATH="$(ls ${MYHDL_TOVERILOG_PATH})";

if [ ! -f "${MYHDL_TOVERILOG_RESOLVED_PATH}" ]; then
	echo "Ensure there is a .venv with myhdl installed; looking for ${MYHDL_TOVERILOG_PATH}";
	exit 1;
fi

patch "${MYHDL_TOVERILOG_RESOLVED_PATH}" <<EOF
--- tristate-error _toVerilog.py	2022-03-09 08:12:19.291980116 +0000
+++ tristate-capable _toVerilog.py	2022-03-09 08:12:06.463972381 +0000
@@ -480,6 +480,9 @@
     # write size for large integers (beyond 32 bits signed)
     # with some safety margin
     # XXX signed indication 's' ???
+    if n is None:
+        return "'bz"
+
     p = abs(n)
     size = ''
     num = str(p).rstrip('L')
EOF
