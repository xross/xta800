#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -g test_i-a_i-i-a__.xc -o $genDir/test_i-a_i-i-a__.xe > $genDir/test_i-a_i-i-a__.log 2>&1");
}
system("@ARGV load $genDir/test_i-a_i-i-a__.xe " .
       "analyse function test " .
       "print completed_loops " .
       "exit > $genDir/test_i-a_i-i-a__.txt 2>&1");

