#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xc -o $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xe > $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.log 2>&1");
}
system("@ARGV load $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.txt 2>&1");

