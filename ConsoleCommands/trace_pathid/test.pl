#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1a -g test.s -o $genDir/test.xe > $genDir/trace_pathid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "summary simple " .
       "-print trace 1 " .
       "-print trace 0 " .
       "exit > $genDir/trace_pathid.txt 2>&1");

