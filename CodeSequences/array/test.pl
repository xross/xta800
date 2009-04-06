#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/array.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "-print src - 0 " .
       "-print src - 1 " .
       "exit > $genDir/array.txt 2>&1");

