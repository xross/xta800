#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/while_loop.log 2>&1");
system("@ARGV load $genDir/test.xe function main summary SIMPLE summary COMPLETE_LOOPS src 0 test.xc src 1 test.xc exit > $genDir/while_loop.txt 2>&1");

