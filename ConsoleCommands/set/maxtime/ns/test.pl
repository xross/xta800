#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/ns.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .

       "config proc 0 0 " .
       "analyse function test0 " .
       "config proc 0 1 " .
       "analyse function test1 " .

       "set maxtime 0 ns 10 " .
       "set maxtime 1 ns 10 " .
       "print summary " .

       "set maxtime 0 ns 1000 " .
       "set maxtime 1 ns 10 " .
       "print summary " .

       "set maxtime 0 ns 1000 " .
       "set maxtime 1 ns 1000 " .
       "print summary " .

       "exit > $genDir/ns.txt 2>&1");

