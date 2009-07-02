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
       "set loop - outer_loop 10 " .
       "set loop - inner_loop 55 " .
       "set loopscope - inner_loop a " .
       "print structure - " .
       "-exit > $genDir/dependent_nested_loop.txt 2>&1");

