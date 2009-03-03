#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_d-aw-i-s-e-aa_e-aa__s-aa____.xc -o $genDir/test_d-aw-i-s-e-aa_e-aa__s-aa____.xe > $genDir/test_d-aw-i-s-e-aa_e-aa__s-aa____.log 2>&1");
}
system("@ARGV load $genDir/test_d-aw-i-s-e-aa_e-aa__s-aa____.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_d-aw-i-s-e-aa_e-aa__s-aa____.txt 2>&1");

