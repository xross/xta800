#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_f-a_.xc -o $genDir/test_f-a_.xe > $genDir/test_f-a_.log 2>&1");
}
system("@ARGV load $genDir/test_f-a_.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_f-a_.txt 2>&1");

