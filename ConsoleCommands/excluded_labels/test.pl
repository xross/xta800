#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/excluded_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add label L1 0x100d0 " .
       "add label L2 0x100d4 " .
       "add filter L1 " .
       "list filter " .
       "analyse function main " .
       "summary simple " .
       "print src 2 " .
       "remove filter L1 " .
       "list filter " .
       "analyse function main " .
       "summary simple " .
       "print src 2 " .
       "print src 1 " .
       "exit > $genDir/excluded_labels.txt 2>&1");
