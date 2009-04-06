#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopmax - outer_loop 10 " .
       "set loopscope - inner_loop a " .
       "set loopmax - inner_loop 55 " .
       "print completed_loops " .
       "-exit > $genDir/dependent_nested_loop.txt 2>&1");

