#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/nested_select.log 2>&1");
}
system("@ARGV load $genDir/test.xe function main summary COMPLETE exit > $genDir/nested_select.txt 2>&1");

