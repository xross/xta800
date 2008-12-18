#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/par_straight_line_code.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "summary complete_loops " .
       "exit > $genDir/par_straight_line_code.txt 2>&1");

