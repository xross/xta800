#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/same_loop_twice.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print completed_loops " .
       "print src - 2 " .
       "print src - 1 " .
       "print src - 0 " .
       "exit > $genDir/same_loop_twice.txt 2>&1");

