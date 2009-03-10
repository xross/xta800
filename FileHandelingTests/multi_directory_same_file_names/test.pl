#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc up/test.xc -o $genDir/test.xe > $genDir/multi_directory_same_file_names.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list sources " .
       "analyse function main " .
       "print src - 0 " .
       "exit  > $genDir/multi_directory_same_file_names.txt 2>&1");

