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
       "config threads 0 0 1 " .
       "-analyze function test0 " .
       "-analyze function test1 " .

       "-set required 0 1000 mhz " .
       "-set required 1 1000 mhz " .
       "print summary " .

       "-set required 0 1 mhz " .
       "-set required 1 1000 mhz " .
       "print summary " .

       "-set required 0 1 mhz " .
       "-set required 1 1 mhz " .
       "print summary " .

       "-exit > $genDir/mhz.txt 2>&1");

