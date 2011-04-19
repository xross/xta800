#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/section_within_section_within_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loop - loop 1 " .
       "print structure - " .
       "-exit > $genDir/section_within_section_within_loop.txt 2>&1");

