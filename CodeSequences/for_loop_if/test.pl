#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/for_loop_if.log 2>&1");
}
system("@ARGV load $genDir/test.xe function main summary SIMPLE summary COMPLETE_LOOPS src 2 test.xc src 4 test.xc src 3 test.xc exit > $genDir/for_loop_if.txt 2>&1");

