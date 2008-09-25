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
       "loopi 0 7 r 0 " .
       "loopi 1 7 r 1 " .
       "loopi 2 7 r 2 " .
       "loopi 3 7 r 3 " .
       "loopi 4 7 r 4 " .
       "loopi 5 7 r 5 " .
       "loopi 6 7 r 6 " .
       "loopi 7 7 r 7 " .
       "loopi 8 7 r 8 " .
       "loopi 9 7 r 9 " .
       "loopi 10 7 r 10 " .
       "loopi 11 7 r 11 " .
       "loopi 12 7 r 12 " .
       "loopi 13 7 r 13 " .
       "loopi 14 7 r 14 " .
       "loopi 15 7 r 15 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");

