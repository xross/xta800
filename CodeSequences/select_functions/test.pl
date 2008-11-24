#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/select_functions.log 2>&1");
}
system("@ARGV load $genDir/test.xe path 0x101a2 0x10192 summary SIMPLE summary COMPLETE summary COMPLETE_LOOPS summary COMPLETE_INSTRUCTIONS exit > $genDir/select_functions.txt 2>&1");

