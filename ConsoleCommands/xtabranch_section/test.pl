#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1a -nostartfiles -g test.s -o $genDir/test.xe > $genDir/xtabranch_section.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list branch " .
       "exit > $genDir/xtabranch_section.txt 2>&1");

