#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/function_call.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "-summary simple " .
       "-summary functions " .
       "-exit > $genDir/function_call.txt 2>&1");

