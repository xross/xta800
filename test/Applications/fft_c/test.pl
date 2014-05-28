#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  system("xcc -target=$target -march=$arch -g -w -O3 fft.c -o $genDir/test.xe > $genDir/fft_c.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function fix_fft_64 " .
       "print structure - " .
       "-exit > $genDir/fft_c.txt 2>&1");

