#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/summary_functions.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary FUNCTIONS " .
       "exit > $genDir/summary_functions.txt 2>&1");

