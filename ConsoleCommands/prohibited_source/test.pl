#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/prohibited_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add prohibited test.xc:7 " .
       "list prohibited " .
       "analyse function main " .
       "summary simple " .
       "print src 2 test.xc " .
       "remove prohibited test.xc:7 " .
       "list prohibited " .
       "add prohibited test.xc:11 " .
       "list prohibited " .
       "analyse function main " .
       "summary simple " .
       "print src 1 test.xc " .
       "exit > $genDir/prohibited_source.txt 2>&1");
