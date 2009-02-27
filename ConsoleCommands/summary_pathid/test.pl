#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.s -o $genDir/test.xe > $genDir/summary_pathid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "summary info 0 " .
       "summary info 1 " .
       "exit > $genDir/summary_pathid.txt 2>&1");

