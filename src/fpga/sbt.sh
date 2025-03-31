#!/bin/bash
THIS_DIR=$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")
PATH=$PATH:${THIS_DIR}/src/thirdparty/oss-cad-suite/bin sbt $*
