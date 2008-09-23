#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -march=xs1a -g test.s -o $genDir/test.xe > $genDir/trace_complete.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "trace COMPLETE " .
       "exit > $genDir/trace_complete.txt 2>&1");

