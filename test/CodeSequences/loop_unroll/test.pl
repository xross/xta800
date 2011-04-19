#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -g -O3 test.xc -o $genDir/test.xe > $genDir/loop_unroll.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-print summary " .
       "-list srclabels " .
       "-exit > $genDir/loop_unroll.txt 2>&1");
