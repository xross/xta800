#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/view_xc_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print src - " .
       "-exit  > $genDir/view_xc_source.txt 2>&1");

