#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -std=c99 test.c -o $genDir/test.xe > $genDir/quicksort.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function sort " .
       "print completed_loops " .
       "print unresolved_paths " .
       "exit > $genDir/quicksort.txt 2>&1");

