#!/usr/bin/perl
use strict;
use warnings;

system("xcc -march=xs1b -g -c add/test.xc -o add.o");
system("xcc -march=xs1b -g -c test.xc -o test.o");
system("xcc -march=xs1b -g test.o add.o -o test.xe");
