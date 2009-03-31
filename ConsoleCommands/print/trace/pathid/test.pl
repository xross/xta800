#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.s -o $genDir/test.xe > $genDir/pathid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopmax - loop 5 " .
       "print summary " .
       "print trace - 1 " .
       "print trace - 0 " .
       "exit > $genDir/pathid.txt 2>&1");

