#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/load_store.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "function main " .
       "src 4 test.xc " .
       "src 3 test.xc " .
       "src 6 test.xc " .
       "src 5 test.xc " .
       "exit > $genDir/load_store.txt 2>&1");

