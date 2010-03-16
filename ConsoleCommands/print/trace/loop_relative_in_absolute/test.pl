#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/loop_relative_in_absolute.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyze function main " .
       "set loop - loopR 5 " .
       "set loopscope - loopA absolute " .
       "set loop - loopA 5 " .
       "-print trace - " .
       "-exit > $genDir/loop_relative_in_absolute.txt 2>&1");

