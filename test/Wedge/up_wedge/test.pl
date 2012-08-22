#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/up_wedge.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyse function main " .
       "-set loop - path_1 3 " .
       "-set loop - path_2 7 " .
       "-set loop - path_3 5 " .
       "-print structure - " .
       "-exit > $genDir/up_wedge.txt 2>&1");

