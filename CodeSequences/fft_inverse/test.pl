#!/usr/bin/perl
use strict;
use warnings;
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
system("xcc -march=$arch -g fft.xc -o $genDir/test.xe > $genDir/fft_inverse.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function fix_fft_64_inverse " .
       "summary simple " .
       "summary complete_loops " .
       "exit > $genDir/fft_inverse.txt 2>&1");

