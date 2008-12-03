#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "loop 1 r 5 " .
       "loop 7 r 3 " .
       "summary COMPLETE_LOOPS " .
       "src 1 test.xc " .
       "src 7 test.xc " .
       "src 10 test.xc " .
       "exit > $genDir/loop.txt 2>&1");

