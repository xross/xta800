#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/worst_case.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function main " .
       "-config case worst " .
       "-print trace - " .
       "-exit > $genDir/worst_case.txt 2>&1");

