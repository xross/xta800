#!/usr/bin/perl
use strict;
use warnings;

system("xcc -target=XS1-L1 -g -c add/test.xc -o add.o");
system("xcc -target=XS1-L1 -g -c test.xc -o test.o");
system("xcc -target=XS1-L1 -g test.o add.o -o test.xe");
