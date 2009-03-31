#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_s-i-a_w-i-a_i-a_i-s-aa____.xc -o $genDir/test_s-i-a_w-i-a_i-a_i-s-aa____.xe > $genDir/test_s-i-a_w-i-a_i-a_i-s-aa____.log 2>&1");
}
system("@ARGV load $genDir/test_s-i-a_w-i-a_i-a_i-s-aa____.xe " .
       "analyse function test " .
       "print completed_loops " .
       "exit > $genDir/test_s-i-a_w-i-a_i-a_i-s-aa____.txt 2>&1");

