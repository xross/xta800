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
       "summary simple " .
       "summary complete_loops " .
       "set loop 4 r 65 " .
       "set loop 3 r 55 " .
       "set loop 5 r 98 " .
       "summary complete_loops " .
       "exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

