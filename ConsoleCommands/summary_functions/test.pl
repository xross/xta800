#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/summary_functions.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary functions " .
       "exit > $genDir/summary_functions.txt 2>&1");

