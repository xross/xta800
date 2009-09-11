#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/ns.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "config threads 1 " .
       "config proc 0 0 " .
       "analyse function test0 " .
       "config proc 0 1 " .
       "analyse function test1 " .

       "-set required 0 10 ns " .
       "-set required 1 10 ns " .
       "print summary " .

       "-set required 0 1000 ns " .
       "-set required 1 10 ns " .
       "print summary " .

       "-set required 0 1000 ns " .
       "-set required 1 1000 ns " .
       "print summary " .

       "-exit > $genDir/ns.txt 2>&1");

