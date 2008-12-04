#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/label.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "label 0x1019a bob " .
       "label bob bill " .
       "function main " .
       "summary SIMPLE " .
       "exit > $genDir/label.txt 2>&1");

