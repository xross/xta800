#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_e-e-s-i-a_i-a_i-w-a__d-a__a_f-a__.xc -o $genDir/test_e-e-s-i-a_i-a_i-w-a__d-a__a_f-a__.xe > $genDir/test_e-e-s-i-a_i-a_i-w-a__d-a__a_f-a__.log 2>&1");
}
system("@ARGV load $genDir/test_e-e-s-i-a_i-a_i-w-a__d-a__a_f-a__.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_e-e-s-i-a_i-a_i-w-a__d-a__a_f-a__.txt 2>&1");

