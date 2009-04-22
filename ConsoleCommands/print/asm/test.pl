#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.s -o $genDir/test.xe > $genDir/asm.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "-print asm 0 0 " .
       "exit > $genDir/asm.txt 2>&1");

