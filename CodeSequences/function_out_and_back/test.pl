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
       "path 0x10192 0x10190 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/function_out_and_back.txt 2>&1");

