#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/valid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "analyse function test0 " .
       "status " .
       "print summary " .
       "config proc 0 1 " .
       "analyse function test1 " .
       "status " .
       "print summary " .
       "config proc 0 2 " .
       "analyse function test2 " .
       "status " .
       "print summary " .
       "config proc 0 3 " .
       "analyse function test3 " .
       "status " .
       "print summary " .
       "config proc 0 0 " .
       "status " .
       "exit > $genDir/valid.txt 2>&1");

