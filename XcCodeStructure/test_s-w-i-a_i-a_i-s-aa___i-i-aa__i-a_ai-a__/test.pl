#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_s-w-i-a_i-a_i-s-aa___i-i-aa__i-a_ai-a__.xc -o $genDir/test_s-w-i-a_i-a_i-s-aa___i-i-aa__i-a_ai-a__.xe > $genDir/test_s-w-i-a_i-a_i-s-aa___i-i-aa__i-a_ai-a__.log 2>&1");
}
system("@ARGV load $genDir/test_s-w-i-a_i-a_i-s-aa___i-i-aa__i-a_ai-a__.xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_s-w-i-a_i-a_i-s-aa___i-i-aa__i-a_ai-a__.txt 2>&1");

