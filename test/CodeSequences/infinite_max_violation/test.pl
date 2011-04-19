#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles test.s -o $genDir/test.xe > $genDir/infinite_max_violation.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test1 " .
       "set required - 50 ns " .
       "analyse function test2 " .
       "set required - 50 ns " .
       "print summary " .
       "-exit > $genDir/infinite_max_violation.txt 2>&1");

