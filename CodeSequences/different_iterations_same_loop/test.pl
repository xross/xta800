#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/different_iterations_same_loop.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "loop 0 7 0 " .
       "loop 1 7 1 " .
       "loop 2 7 2 " .
       "loop 3 7 3 " .
       "loop 4 7 4 " .
       "loop 5 7 5 " .
       "loop 6 7 6 " .
       "loop 7 7 7 " .
       "loop 8 7 8 " .
       "loop 9 7 9 " .
       "loop 10 7 10 " .
       "loop 11 7 11 " .
       "loop 12 7 12 " .
       "loop 13 7 13 " .
       "loop 14 7 14 " .
       "loop 15 7 15 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");

