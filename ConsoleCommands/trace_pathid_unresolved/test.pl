#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/trace_pathid_unresolved.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "summary simple " .
       "-print trace 0 " .
       "exit > $genDir/trace_pathid_unresolved.txt 2>&1");

