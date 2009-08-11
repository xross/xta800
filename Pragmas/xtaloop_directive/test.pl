#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/xtaloop_directive.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list srcloops " .
       "analyse function main " .
       "print structure - " .
       "-exit > $genDir/xtaloop_directive.txt 2>&1");

