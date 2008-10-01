#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/function_call.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "summary FUNCTIONS " .
       "exit > $genDir/function_call.txt 2>&1");

