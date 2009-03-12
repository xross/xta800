#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g tutorial_gui.xc -o $genDir/test.xe > $genDir/gui.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function resetTable " .
       "analyse function functionCalls " .
       "analyse function main " .
       "print completed_paths " .
       "exit > $genDir/gui.txt 2>&1");
