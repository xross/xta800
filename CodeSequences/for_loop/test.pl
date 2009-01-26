#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/for_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe analyse function main summary simple summary complete_loops print src 1 test.xc print src 2 test.xc exit > $genDir/for_loop.txt 2>&1");

