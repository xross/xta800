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
       "add filter 0x100d0 " .
       "list filter " .
       "analyse function main " .
       "summary simple " .
       "print src 2 test.xc " .
       "remove filter 0x100d0 " .
       "list filter " .
       "add filter 0x100d4 " .
       "list filter " .
       "analyse function main " .
       "summary simple " .
       "print src 1 test.xc " .
       "exit > $genDir/excluded.txt 2>&1");
