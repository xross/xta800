#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/multiroute.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function main " .
       "-set instructiontime - main 100.0 ns " .
       "-print structure - " .
       "-config threads 0 0 1 " .
       "-set instructiontime - main 100.0 ns " .
       "-print structure - " .
       "-exit > $genDir/multiroute.txt 2>&1");

