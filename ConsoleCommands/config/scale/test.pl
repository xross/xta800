#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/scale.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyse function main " .
       "-set loop - l 10000 " .
       "-print structure - " .
       "-config scale false " .
       "-print structure - " .
       "-config scale true " .
       "-print structure - " .
       "-exit > $genDir/scale.txt 2>&1");

