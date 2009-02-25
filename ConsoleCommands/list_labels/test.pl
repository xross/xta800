#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -nostartfiles test.s -o $genDir/test.xe > $genDir/list_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list labels " .
       "exit > $genDir/list_labels.txt 2>&1");

