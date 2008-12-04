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
       "function main " .
       "summary SIMPLE " .
       "summary COMPLETE_LOOPS " .
       "loop 6 r 10 " .
       "loop 13 a 55 " .
       "loop 9 r 1 " .
       "loop 12 a 1100 " .
       "loop 8 r 20 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

