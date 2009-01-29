#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/prohibited.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add prohibited 0x100d0 " .
       "list prohibited " .
       "analyse function main " .
       "summary simple " .
       "print src 2 " .
       "remove prohibited 0x100d0 " .
       "list prohibited " .
       "add prohibited 0x100d4 " .
       "list prohibited " .
       "analyse function main " .
       "summary simple " .
       "print src 1 " .
       "exit > $genDir/prohibited.txt 2>&1");
