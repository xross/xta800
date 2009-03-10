#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/cycles.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .

       "config proc 0 0 " .
       "analyse function test0 " .
       "config proc 0 1 " .
       "analyse function test1 " .

       "set maxtime 0 cycles 1 " .
       "set maxtime 1 cycles 1 " .
       "print summary " .

       "set maxtime 0 cycles 100 " .
       "set maxtime 1 cycles 1 " .
       "print summary " .

       "set maxtime 0 cycles 100 " .
       "set maxtime 1 cycles 100 " .
       "print summary " .

       "exit > $genDir/cycles.txt 2>&1");

