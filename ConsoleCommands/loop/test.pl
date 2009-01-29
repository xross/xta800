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
       "analyse function main " .
       "set loop 1 r 5 " .
       "set loop 7 r 3 " .
       "summary complete_loops " .
       "print src 1 " .
       "print src 7 " .
       "print src 10 " .
       "exit > $genDir/loop.txt 2>&1");

