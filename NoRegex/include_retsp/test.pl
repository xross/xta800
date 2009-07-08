#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/include_retsp.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print trace - 0 " .
       "-exit > $genDir/include_retsp.txt 2>&1");

