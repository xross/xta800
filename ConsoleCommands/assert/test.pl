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
       "config proc 0 1 " .
       "analyse function test1 " .
       "assert maxcycles 1 " .
       "assert maxtime 10 " .
       "assert maxcycles 10 " .
       "assert maxtime 600 " .
       "analyse function _done " .
       "assert maxtime 600 " .
       "config freq 100 " .
       "assert maxtime 600 " .
       "config proc 0 0 " .
       "analyse function test0 " .
       "assert maxcycles 1 " .
       "assert maxtime 10 " .
       "assert maxcycles 10 " .
       "assert maxtime 200 " .
       "config proc 0 1 " .
       "assert maxtime 200 " .
       "exit > $genDir/assert.txt 2>&1");

