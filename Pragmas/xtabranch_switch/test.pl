#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -O2 -march=xs1a -g test.xc -o $genDir/test.xe > $genDir/xtabranch_switch.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list branches " .
       "exit > $genDir/xtabranch_switch.txt 2>&1");

