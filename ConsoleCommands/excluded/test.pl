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
       "add excluded 0x100d0 " .
       "list excluded " .
       "analyse function main " .
       "summary simple " .
       "print src 2 ../test.xc " .
       "remove excluded 0x100d0 " .
       "list excluded " .
       "add excluded 0x100d4 " .
       "list excluded " .
       "analyse function main " .
       "summary simple " .
       "print src 1 ../test.xc " .
       "exit > $genDir/excluded.txt 2>&1");
