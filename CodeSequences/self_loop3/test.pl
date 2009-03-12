#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -nostartfiles test.s -o $genDir/test.xe > $genDir/self_loop3.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print completed_loops " .
       "exit > $genDir/self_loop3.txt 2>&1");

