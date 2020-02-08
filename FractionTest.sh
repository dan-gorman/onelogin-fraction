#!/usr/bin/env bash
#
BASEDIR=$(dirname "$0")
RELAPPJAR=out/artifacts/FractionTest_jar/FractionTest.jar
ABSAPPJAR=${BASEDIR%%/}${BASEDIR:+/}$RELAPPJAR
java -jar ${ABSAPPJAR} $@
