#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/functions.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "print functions - " .
       "-analyse function main " .
       "print functions - " .
       "-exit > $genDir/functions.txt 2>&1");

