#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -g test_s-aaaa_.xc -o $genDir/test_s-aaaa_.xe > $genDir/test_s-aaaa_.log 2>&1");
}
system("@ARGV load $genDir/test_s-aaaa_.xe " .
       "analyse function test " .
       "print completed_loops " .
       "exit > $genDir/test_s-aaaa_.txt 2>&1");

