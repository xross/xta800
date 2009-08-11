#!/usr/bin/perl
use strict;
use warnings;

my $path = "C:/peter/git/software/verif_tests_sw/Tools/xta/ConsoleCommands/analyse/path/by_type/path_windows";

system("xcc -march=xs1b -g -c $path/add/test.xc -o add/test.o");
system("xcc -march=xs1b -g -c $path/test.xc -o test.o");
system("xcc -march=xs1b -g add/test.o test.o -o test.xe");
