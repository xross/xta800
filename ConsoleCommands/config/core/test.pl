#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/core.log 2>&1");
}
system("@ARGV load $genDir/test.xe config core 0 2 -status config core 0 1 -status config core stdcore[2] -status exit > $genDir/core.txt 2>&1");

