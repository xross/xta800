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
       "excluded add 0x1019c " .
       "function main " .
       "summary SIMPLE " .
       "src 2 test.xc " .
       "excluded remove 0x1019c " .
       "excluded add 0x101a0 " .
       "function main " .
       "summary SIMPLE " .
       "src 1 test.xc " .
       "exit > $genDir/excluded.txt 2>&1");