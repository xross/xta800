#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1a -g test.s -o $genDir/test.xe > $genDir/asm.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "asm 2 " .
       "asm 1 " .
       "exit > $genDir/asm.txt 2>&1");

