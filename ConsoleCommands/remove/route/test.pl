#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/route.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test0 " .
       "analyse function test1 " .
       "print summary " .
       "remove route 0 " .
       "print summary " .
       "remove route 1 " .
       "print summary " .
       "exit > $genDir/route.txt 2>&1");

