#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/required_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add label 0x100cc L1 " .
       "add label 0x100d0 L2 " .
       "add required L1 " .
       "list required " .
       "analyse function test " .
       "summary simple " .
       "-print src 0 " .
       "remove required L1 " .
       "list required " .
       "add required L2 " .
       "list required " .
       "analyse function test " .
       "summary simple " .
       "-print src 1 " .
       "exit > $genDir/required_labels.txt 2>&1");
