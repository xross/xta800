#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1b -g test.xc up/up.xc -o $genDir/test.xe > $genDir/multi_directory_with_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe list label analyse function flashled print src 0 ../test.xc config proc 0 1 list label analyse function led2_thread print src 7 ../up/up.xc print src 4 ../up/up.xc config proc 0 2 list label config proc 0 3 list label exit  > $genDir/multi_directory_with_labels.txt 2>&1");

