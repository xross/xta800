#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles -march=$arch -g test.s -o $genDir/test.xe > $genDir/loop_in_path.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config threads stdcore[0] 1 " .
       "analyze function main " .
       "-print trace - " .
       "-set loop - loop 10 " .
       "-print trace - " .
       "-exit > $genDir/loop_in_path.txt 2>&1");

