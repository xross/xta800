#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch -fxta-info -g test_e-aaaa_.xc -o $genDir/test_e-aaaa_.xe > $genDir/test_e-aaaa_.log 2>&1");
}
system("@ARGV load $genDir/test_e-aaaa_.xe " .
       "analyse function test " .
       "print completed_loops " .
       "exit > $genDir/test_e-aaaa_.txt 2>&1");

