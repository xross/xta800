#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=xs1b -g test.xc up/test.xc -o $genDir/test.xe > $genDir/multi_directory_same_file_names_with_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list label " .
       "analyse function main " .
       "print src 0 test.xc " .
       "print src 0 up/test.xc " .
       "exit  > $genDir/multi_directory_same_file_names_with_labels.txt 2>&1");

