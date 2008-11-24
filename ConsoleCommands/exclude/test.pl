#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/exclude.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "exclude test.xc:11 " . 
       "function main " .
       "summary SIMPLE " .
       "src 1 test.xc " .
       "exit > $genDir/exclude.txt 2>&1");

