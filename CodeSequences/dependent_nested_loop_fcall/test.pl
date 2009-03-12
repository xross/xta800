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
       "print completed_loops " .
       "set loop - 2 r 65 " .
       "set loop - 1 r 55 " .
       "print completed_loops " .
       "-exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

