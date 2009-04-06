#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -fxta-info -g test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xc -o $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xe > $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.log 2>&1");
}
system("@ARGV load $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.xe " .
       "analyse function test " .
       "print completed_loops " .
       "exit > $genDir/test_i-a_i-a_i-i-a_i-a__w-i-a_i-a_w-i-a___.txt 2>&1");

