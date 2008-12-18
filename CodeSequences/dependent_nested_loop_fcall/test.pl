#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop_fcall.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "summary complete_loops " .
       "set loop 6 r 10 " .
       "set loop 13 a 55 " .
       "set loop 9 r 1 " .
       "set loop 12 a 1100 " .
       "set loop 8 r 20 " .
       "summary complete_loops " .
       "exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

