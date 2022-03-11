#!/bin/bash
set -e;
THISDIR="$(dirname "$(readlink -f "$0")")";
VENV_ACTIVATE="../.venv/bin/activate";
OUTDIR="./out";
MODULE_NAME="Bootloader";
MODULE_VERILOG_FILENAME="${OUTDIR}/${MODULE_NAME}.v";
HDLDIR="./src/hdl";
OUT_YOSYS_JSON_FILENAME="${OUTDIR}/${MODULE_NAME}.json";
OUT_PNR_ASC_FILENAME="${OUTDIR}/${MODULE_NAME}.asc";
OUT_TIMINGS_FILENAME="${OUTDIR}/${MODULE_NAME}.timings";
OUT_PACK_BIN_FILENAME="${OUTDIR}/${MODULE_NAME}.bin";
PACKAGE="tq144"
CLK_FREQUENCY_MHZ="12"

if [ ! -f "${VENV_ACTIVATE}" ]; then
	echo "Ensure there is a .venv; looking for ${VENV_ACTIVATE}";
	exit 1;
fi

cd "${THISDIR}";
mkdir -p "${OUTDIR}";
cp ${HDLDIR}/*.pcf "${OUTDIR}";

. "${VENV_ACTIVATE}";
python -m src.bootloader "${OUTDIR}";

yosys -q -p "synth_ice40 -relut -top ${MODULE_NAME} -json ${OUT_YOSYS_JSON_FILENAME}" ${MODULE_VERILOG_FILENAME} ${HDLDIR}/**/*.v;
nextpnr-ice40 --force --json ${OUT_YOSYS_JSON_FILENAME} --pcf ${OUTDIR}/board.pcf --asc ${OUT_PNR_ASC_FILENAME} --freq ${CLK_FREQUENCY_MHZ} --hx1k --package ${PACKAGE} --opt-timing;

icetime -p ${OUTDIR}/board.pcf -P ${PACKAGE} -r ${OUT_TIMINGS_FILENAME} -d hx1k -t ${OUT_PNR_ASC_FILENAME}

icepack -s ${OUT_PNR_ASC_FILENAME} ${OUT_PACK_BIN_FILENAME}
