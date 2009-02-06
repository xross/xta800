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
       "set loop 2 r 5 " .
       "set loop 8 r 3 " .
       "summary complete_loops " .
       "print src 2 " .
       "print src 8 " .
       "print src 13 " .
       "exit > $genDir/loop.txt 2>&1");

