#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/par_simple.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "path 0x101a2 0x10192 " .
       "-summary simple " .
       "-summary complete " .
       "-summary complete_loops " .
       "-summary complete_instructions " .
       "-exit > $genDir/par_simple.txt 2>&1");

