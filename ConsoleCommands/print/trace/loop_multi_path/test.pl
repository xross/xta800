#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/loop_multi_path.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loop - loop 5 " .
       "set looppath - else 3 " .
       "set looppath - case4 1 " .
       "set looppath - case3 1 " .
       "set looppath - case2 1 " .
       "set looppath - case1 1 " .
       "-print trace - " .
       "-exit > $genDir/loop_multi_path.txt 2>&1");

