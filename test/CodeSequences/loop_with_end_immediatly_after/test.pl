#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -O3 -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/loop_with_end_immediatly_after.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyze path start stop " .
       "-print structure - " .
       "-exit > $genDir/loop_with_end_immediatly_after.txt 2>&1");

