#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dynamic_different_iterations_same_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
        "mode DYNAMIC " .
       "function main " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/dynamic_different_iterations_same_loop.txt 2>&1");
