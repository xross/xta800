#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1a -g test.s -o $genDir/test.xe > $genDir/load_with_no_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "status " .
       "exit > $genDir/load_with_no_source.txt 2>&1");

