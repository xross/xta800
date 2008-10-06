#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/different_iterations_same_loop.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "loopi 12 r 0 " .
       "loopi 13 r 1 " .
       "loopi 14 r 2 " .
       "loopi 15 r 3 " .
       "loopi 16 r 4 " .
       "loopi 17 r 5 " .
       "loopi 18 r 6 " .
       "loopi 19 r 7 " .
       "loopi 20 r 8 " .
       "loopi 21 r 9 " .
       "loopi 22 r 10 " .
       "loopi 23 r 11 " .
       "loopi 24 r 12 " .
       "loopi 25 r 13 " .
       "loopi 26 r 14 " .
       "loopi 27 r 15 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");

