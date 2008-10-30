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
        "loop 1 r 10 " .
        "loop 4 r 20 " .
        "loopi 8 r 10 " .
        "loopi 20 r 10 " .
        "loopi 12 r 20 " .
        "loopi 16 r 20 " .
        "loopi 24 r 20 " .
        "loopi 28 r 20 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");
