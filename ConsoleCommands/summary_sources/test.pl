#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g main.xc test1.xc test2.xc test3.xc test4.xc test5.xc -o $genDir/test.xe > $genDir/summary_sources.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SOURCES " .
       "exit > $genDir/summary_sources.txt 2>&1");

