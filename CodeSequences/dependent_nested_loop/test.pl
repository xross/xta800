#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "set loop 4 r 10 " .
       "set loop 3 a 55 " .
       "summary complete_loops " .
       "exit > $genDir/dependent_nested_loop.txt 2>&1");

