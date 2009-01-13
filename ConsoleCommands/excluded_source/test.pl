#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/excluded_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add excluded ../test.xc:7 " .
       "list excluded " .
       "analyse function main " .
       "summary simple " .
       "print src 2 ../test.xc " .
       "remove excluded ../test.xc:7 " .
       "list excluded " .
       "add excluded ../test.xc:11 " .
       "list excluded " .
       "analyse function main " .
       "summary simple " .
       "print src 1 ../test.xc " .
       "exit > $genDir/excluded_source.txt 2>&1");
