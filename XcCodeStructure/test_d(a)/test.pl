#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_d(a).xc -o $genDir/test_d(a).xe > $genDir/test_d(a).log 2>&1");
}
system("@ARGV load $genDir/test_d(a).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_d(a).txt 2>&1");

