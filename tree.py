#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os
import os.path
import re

def run_bash(cmd):
	stream = os.popen(cmd)
	output = stream.read()
	return output

def read(filename):
        with open(filename) as f:
                lines = f.readlines()
        x = [x[:-1] for x in lines]
        return x

def both_in_line(line, *chars):
	for c in chars:
		if c not in line:
			return False
	return True

def main():
	# make README.md
	run_bash('sh tree.sh > README.md')
	# solve
	README = read('./README.md')
	for line in README:
		# need to solve
		if both_in_line(line, '*', '[', ']', '(', ')'):
			# python3
			# >>> import re
			# >>> line = " * [ALGORITHM_DATASTRUCTURE](./ALGORITHM_DATASTRUCTURE)"
			# >>> re.split('\[|\]|\(|\)', line)
			# [' * ', 'ALGORITHM_DATASTRUCTURE', '', './ALGORITHM_DATASTRUCTURE', '']
			t = re.split('\[|\]|\(|\)', line)
			file_path = t[3]
			if os.path.isdir(file_path) and not os.path.isfile(file_path + "/README.md"):
				line = t[0] + t[1]
		print(line)

main()
