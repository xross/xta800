#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g -gsource test.xc -o $genDir/test.xe > $genDir/source_test.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "-summary simple " .
       "-summary complete " .
       "-summary complete_loops " .
       "-summary complete_instructions " .
       "-print src 1 " .
       "-print src 0 " .
       "-exit > $genDir/source_test.txt 2>&1");

