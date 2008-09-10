#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.s -o $genDir/test.xe > $genDir/trace.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "trace " .
       "exit > $genDir/trace.txt 2>&1");

