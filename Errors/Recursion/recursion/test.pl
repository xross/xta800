#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/recursion.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyse function main " .
       "-print structure - " .
       "-analyse function f " .
       "-print structure - " .
       "-analyse function g " .
       "-print structure - " .
       "-exit > $genDir/recursion.txt 2>&1");

