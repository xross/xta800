#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/array.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "src 1 test.xc " .
       "src 2 test.xc " .
       "exit > $genDir/array.txt 2>&1");

