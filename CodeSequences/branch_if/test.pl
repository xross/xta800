#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
    system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/branch_if.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "summary COMPLETE " .
       "summary COMPLETE_LOOPS " .
       "summary COMPLETE_INSTRUCTIONS " .
       "src 1 test.xc " .
       "src 2 test.xc " .
       "exit > $genDir/branch_if.txt 2>&1");

