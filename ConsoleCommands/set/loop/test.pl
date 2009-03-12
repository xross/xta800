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
       "analyse function test " .
       "set loop - 3 r 5 " .
       "set loop - 2 r 3 " .
       "set loop - 5 r 10 " .
       "print completed_loops " .
       "print src - 3 " .
       "print src - 0 " .
       "print src - 5 " .
       "exit > $genDir/loop.txt 2>&1");

