#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -O3 -target=$target -march=$arch -fxta-info -g test.xc -o $genDir/test.xe > $genDir/section_within_loop_within_infiniteloop.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "set loop - loop 10 " .
       "set looppath - if 7 " .
       "-exit > $genDir/section_within_loop_within_infiniteloop.txt 2>&1");


