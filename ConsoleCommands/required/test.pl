#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/required.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add required 0x100d0 " .
       "list required " .
       "analyse function main " .
       "summary simple " .
       "print src 1 " .
       "remove required 0x100d0 " .
       "list required " .
       "add required 0x100d4 " .
       "list required " .
       "analyse function main " .
       "summary simple " .
       "print src 2 " .
       "exit > $genDir/required.txt 2>&1");
