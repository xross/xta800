#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_i-i-i-i-i-i-i-i-a________.xc -o $genDir/test_i-i-i-i-i-i-i-i-a________.xe > $genDir/test_i-i-i-i-i-i-i-i-a________.log 2>&1");
}
system("@ARGV load $genDir/test_i-i-i-i-i-i-i-i-a________.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_i-i-i-i-i-i-i-i-a________.txt 2>&1");

