#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
system("xcc -march=$arch -std=c99 -g test.c -o $genDir/test.xe > $genDir/functional_correctness.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config mode resolve " .
       "analyse function main " .
       "status " .
       "exit > $genDir/functional_correctness.txt 2>&1");

