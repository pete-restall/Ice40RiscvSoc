#!/bin/bash
set -e;
THISDIR="$(dirname "$(readlink -f "$0")")";
RADIANT_HDL_PATH="/opt/lattice/radiant/3.1/cae_library/simulation/verilog/iCE40UP";
CONVERTDEVICESTRING_PATH="${RADIANT_HDL_PATH}/convertDeviceString.v";
PATCHED_HDL_DIR="${THISDIR}/src/thirdparty/lattice/radiant";

if [ ! -f "${CONVERTDEVICESTRING_PATH}" ]; then
	echo "Ensure Lattice Radiant is installed; looking for ${CONVERTDEVICESTRING_PATH}";
	exit 1;
fi

mkdir -p "${PATCHED_HDL_DIR}";
cp "${CONVERTDEVICESTRING_PATH}" "${PATCHED_HDL_DIR}/";
patch "${PATCHED_HDL_DIR}/convertDeviceString.v" <<EOF
--- convertDeviceString.v.ORIGINAL	2022-11-04 20:33:17.830014377 +0000
+++ convertDeviceString.v.PATCHED	2022-11-05 08:57:56.176484403 +0000
@@ -9,7 +9,7 @@
 	reg decimalFlag;
 	reg [255:0] reverseVal;
 	integer concatDec[255:0];
-        reg [1:8] character;
+        reg [7:0] character;
 
         reg [7:0] checkType;
         begin 
@@ -27,7 +27,7 @@
             for (i=MAX_STRING_LENGTH-1; i>=1 ; i=i-1) begin 
                 for (j=1; j<=8; j=j+1) begin 
 
-                    character[j] = attributeValue[i*8-j];
+                    character[8 - j] = attributeValue[i*8-j];
                 end 
 
                 //Check to see if binary or hex
EOF
