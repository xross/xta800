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
       "loopi 1 r 5 " .
       "loopi 4 r 4 " .
       "loopi 7 r 3 " .
       "loopi 10 r 2 " .
       "loopi 13 r 1 " .
       "summary COMPLETE_LOOPS " .
       "src 15 test.xc " .
       "src 1 test.xc " .
       "src 4 test.xc " .
       "src 7 test.xc " .
       "src 10 test.xc " .
       "src 13 test.xc " .
       "exit > $genDir/loop.txt 2>&1");

