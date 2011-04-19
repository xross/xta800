#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/nested_loop_in_path.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function main " .
       "set loop - loop 5 " .
       "set loop - a_loop 3 " .
       "set loop - b_loop 2 " .
       "set looppath - else 3 " .
       "set looppath - a_else 2 " .
       "set looppath - b_else 1 " .
       "-print trace - " .
       "-exit > $genDir/nested_loop_in_path.txt 2>&1");

