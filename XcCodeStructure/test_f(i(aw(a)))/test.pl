#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_f(i(aw(a))).xc -o $genDir/test_f(i(aw(a))).xe > $genDir/test_f(i(aw(a))).log 2>&1");
}
system("@ARGV load $genDir/test_f(i(aw(a))).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_f(i(aw(a))).txt 2>&1");

