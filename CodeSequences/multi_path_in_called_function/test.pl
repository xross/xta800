#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/multi_path_in_called_function.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print completed_loops " .
       "exit > $genDir/multi_path_in_called_function.txt 2>&1");

