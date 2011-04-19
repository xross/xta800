#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/loop_paths.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function main " .
       "set loop - loop 10 " .
       "set looppath - if 5 " .
       "set looppath - else 5 " .
       "-print trace - " .
       "-exit > $genDir/loop_paths.txt 2>&1");

