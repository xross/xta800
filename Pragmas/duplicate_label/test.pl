#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g duplicate_label.xc -o $genDir/test.xe > $genDir/duplicate_label.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "exit > $genDir/duplicate_label.txt 2>&1");

