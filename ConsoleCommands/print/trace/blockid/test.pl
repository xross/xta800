#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.s -o $genDir/test.xe > $genDir/blockid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "-print trace b27 " .
       "-print trace b22 " .
       "exit > $genDir/blockid.txt 2>&1");

