#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/prohibited_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add label 0x100cc L1 " .
       "add label 0x100d0 L2 " .
       "add prohibited L1 " .
       "list prohibited " .
       "analyse function test " .
       "summary simple " .
       "print src 1 " .
       "remove prohibited L1 " .
       "list prohibited " .
       "add prohibited L2 " .
       "list prohibited " .
       "analyse function test " .
       "summary simple " .
       "print src 2 " .
       "exit > $genDir/prohibited_labels.txt 2>&1");
