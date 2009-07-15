#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
system("xcc -target=$target -march=$arch -std=c99 -g test.c -o $genDir/test.xe > $genDir/float_library.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print node_info - - " .
       "exit > $genDir/float_library.txt 2>&1");

