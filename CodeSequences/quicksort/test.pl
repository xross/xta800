#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -std=c99 -g test.c -o $genDir/test.xe > $genDir/quicksort.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function sort " .
       "summary SIMPLE " .
       "summary COMPLETE_LOOPS " .
       "summary UNRESOLVED " .
       "exit > $genDir/quicksort.txt 2>&1");

