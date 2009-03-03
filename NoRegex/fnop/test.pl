#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -nostartfiles test.s -o $genDir/test.xe > $genDir/fnop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "summary info 0 " .
       "summary complete_instructions " .
       "print trace 0 " .
       "-exit > $genDir/fnop.txt 2>&1");

