#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop_fcall.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function loop " .
       "set loopmax - outer_loop 10 " .
       "set loopscope - inner_loop a " .
       "set loopmax - inner_loop 55 " .
       "print completed_loops " .
       "-exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

