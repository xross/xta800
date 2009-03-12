#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/label_label.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path INPUT_1 INPUT_2 " .
       "status " .
       "print summary " .
       "exit > $genDir/label_label.txt 2>&1");

