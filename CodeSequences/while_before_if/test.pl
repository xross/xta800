#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/while_before_if.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/while_before_if.txt 2>&1");

