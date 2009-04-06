#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/pc_pc.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path 0x10002 0x1000c " .
       "status " .
       "print summary " .
       "exit > $genDir/pc_pc.txt 2>&1");

