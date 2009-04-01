#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/more_than_max.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopmax - loop 100 " .

       "set loop - loop_func1 110 " .
       "set loop - loop_func2 150 " .
       "print path_info - 1 " .
       "print path_info - 2 " .

       "set loop - loop_func1 99 " .
       "set loop - loop_func2 2 " .
       "print path_info - 1 " .
       "print path_info - 2 " .

       "exit > $genDir/more_than_max.txt 2>&1");

