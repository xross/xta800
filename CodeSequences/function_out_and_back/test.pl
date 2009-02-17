#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/function_out_and_back.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path 0x100c6 0x100c4 " .
       "-summary complete_loops " .
       "-exit > $genDir/function_out_and_back.txt 2>&1");

