#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/infinite_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "-print trace - " .
       "-exit > $genDir/infinite_loop.txt 2>&1");

