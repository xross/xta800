#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/max_less_than_hard_count.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .

       "set loop - loop_func1 110 " .
       "set loop - loop_func2 140 " .
       "set loopmax - loop 100 " .

       "print path_info - 1 " .
       "print path_info - 2 " .

       "exit > $genDir/max_less_than_hard_count.txt 2>&1");

