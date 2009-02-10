#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -c -g src1/src.xc -o $genDir/src1.o > $genDir/multi_directory_labels.log 2>&1");
        system("xcc -march=$arch -c -g src2/src.xc -o $genDir/src2.o >> $genDir/multi_directory_labels.log 2>&1");
        system("xcc -march=$arch -g main.xc $genDir/src1.o $genDir/src2.o -o $genDir/test.xe >> $genDir/multi_directory_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list labels " .
       "analyse path mainA src1 " .
       "summary simple " .
       "summary complete_instructions " .
       "analyse path mainB src2 " .
       "summary simple " .
       "summary complete_instructions " .
       "exit > $genDir/multi_directory_labels.txt 2>&1");

