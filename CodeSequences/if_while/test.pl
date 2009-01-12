#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/if_while.log 2>&1");
}
system("@ARGV load $genDir/test.xe analyse function main summary simple summary complete_loops print src 2 ../test.xc print src 4 ../test.xc print src 3 ../test.xc exit > $genDir/if_while.txt 2>&1");

