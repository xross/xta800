#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/excluded.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add filter 0x100cc " .
       "list filters " .
       "analyse function test " .
       "summary simple " .
       "-print src 1 " .
       "remove filter 0x100cc " .
       "list filters " .
       "add filter 0x100d0 " .
       "list filters " .
       "summary simple " .
       "-print src 0 " .
       "exit > $genDir/excluded.txt 2>&1");
