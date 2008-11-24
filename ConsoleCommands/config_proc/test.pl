#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1b main.xc func.xc checkCoreId.s -o $genDir/test.xe > $genDir/config_proc.log 2>&1");
}
system("@ARGV load $genDir/test.xe config proc 128 3 status exit > $genDir/config_proc.txt 2>&1");

