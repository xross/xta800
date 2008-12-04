#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dynamic_branch_if.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
        "mode DYNAMIC " .
       "function main " .
       "summary SIMPLE " .
       "summary COMPLETE " .
       "summary COMPLETE_LOOPS " .
       "src 0 test.xc " .
       "exit > $genDir/dynamic_branch_if.txt 2>&1");

