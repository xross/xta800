#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/multi_core.log 2>&1");
}
system("@ARGV load $genDir/test.xe config proc 0 2 status config proc 0 1 status exit > $genDir/multi_core.txt 2>&1");

