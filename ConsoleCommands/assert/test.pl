#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/assert.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "assert maxcycles 1 " .
       "assert maxtime 10 " .
       "assert maxcycles 10 " .
       "assert maxtime 200 " .
       "analyse function _done " .
       "assert maxtime 200 " .
       "exit > $genDir/assert.txt 2>&1");

