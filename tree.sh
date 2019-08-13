#!/bin/bash

#File: tree-md

tree=$(tree -f --noreport -I '*~' --charset ascii $1 |
	sed -e 's/| / /g' -e 's/[|`]-\+/ */g' -e 's:\(* \)\(\(.*/\)\([^/]\+\)\):\1[\4](\2):g' |
	grep -v -x '.' | grep -v 'README.md' | grep -v '         ')

printf "# Project tree\n\n${tree}"
