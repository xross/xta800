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
       "analyse function main " .
       "set loop 1 r 10 " .
       "set loop 3 r 20 " .
       "set loop 7 r 10 " .
       "set loop 11 r 20 " .
       "set loop 14 r 20 " .
       "set loop 18 r 10 " .
       "set loop 22 r 20 " .
       "set loop 25 r 20 " .
       "summary complete_loops " .
       "exit > $genDir/different_iterations_same_loop.txt 2>&1");
