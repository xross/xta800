#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/less_than_max.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopmax - loop 100 " .
       "set loop - loop_func1 50 " .
       "set loop - loop_func2 25 " .
       "print path_info - 1 " .
       "print path_info - 2 " .
       "print completed_loops " .
       "exit > $genDir/less_than_max.txt 2>&1");

