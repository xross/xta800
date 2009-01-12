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
       "config mode resolve " .
       "analyse function main " .
       "summary simple " .
       "summary complete " .
       "summary complete_loops " .
       "print src 0 ../test.xc " .
       "exit > $genDir/dynamic_branch_if.txt 2>&1");

