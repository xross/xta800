#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/looppath_for.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loop - loop 100 " .
       "set looppath - loop_func1 75 " .
       "set looppath - loop_func2 25 " .
       "print structure - " .
       "-exit > $genDir/looppath_for.txt 2>&1");

