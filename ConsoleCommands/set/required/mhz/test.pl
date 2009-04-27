#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/mhz.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .

       "config proc 0 0 " .
       "analyse function test0 " .
       "config proc 0 1 " .
       "analyse function test1 " .

       "-set required 0 1000 mhz " .
       "-set required 1 1000 mhz " .
       "print summary " .

       "-set required 0 1 mhz " .
       "-set required 1 1000 mhz " .
       "print summary " .

       "-set required 0 1 mhz " .
       "-set required 1 1 mhz " .
       "print summary " .

       "-unset required 0 " .
       "-unset required 1 " .
       "print summary " .

       "-exit > $genDir/mhz.txt 2>&1");

