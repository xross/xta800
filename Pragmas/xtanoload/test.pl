#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g main.xc -o $genDir/test.xe > $genDir/xtanoload.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyse function main " .
       "print structure - ".
       "-analyse function test " .
       "print structure - ".
       "-exit > $genDir/xtanoload.txt 2>&1");

