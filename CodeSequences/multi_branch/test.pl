#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -nostartfiles test.s -o $genDir/test.xe > $genDir/multi_branch.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "exit > $genDir/multi_branch.txt 2>&1");

