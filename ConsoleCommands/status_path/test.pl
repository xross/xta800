#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/status_path.log 2>&1");
}
system("@ARGV load $genDir/test.xe path test.xc:3 test.xc:4 status exit > $genDir/status_path.txt 2>&1");

