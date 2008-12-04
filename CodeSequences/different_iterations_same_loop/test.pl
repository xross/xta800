#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/different_iterations_same_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "function main " .
        "loop 1 r 10 " .
        "loop 3 r 20 " .
        "loop 7 r 10 " .
        "loop 11 r 20 " .
        "loop 14 r 20 " .
        "loop 18 r 10 " .
        "loop 22 r 20 " .
        "loop 25 r 20 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");
