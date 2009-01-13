#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g label1.xc -o $genDir/test.xe > $genDir/label1.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path pointA pointB " .
       "summary simple " .
       "summary complete_loops " .
       "exit > $genDir/label1.txt 2>&1");

