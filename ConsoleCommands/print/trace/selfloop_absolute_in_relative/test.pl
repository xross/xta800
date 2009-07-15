#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/selfloop_absolute_in_relative.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loopscope - loopA absolute " .
       "set loop - loopA 15 " .
       "set loop - loopR 5 " .
       "-print trace - " .
       "-exit > $genDir/selfloop_absolute_in_relative.txt 2>&1");

