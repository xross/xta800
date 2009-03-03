#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_e(aaaa).xc -o $genDir/test_e(aaaa).xe > $genDir/test_e(aaaa).log 2>&1");
}
system("@ARGV load $genDir/test_e(aaaa).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_e(aaaa).txt 2>&1");

