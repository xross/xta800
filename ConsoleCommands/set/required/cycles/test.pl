#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/cycles.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .

       "-analyze function test0 " .
       "-analyze function test1 " .

       "-set required 0 1 cycles " .
       "-set required 1 1 cycles " .
       "print summary " .

       "-set required 0 100 cycles " .
       "-set required 1 1 cycles " .
       "print summary " .

       "-set required 0 100 cycles " .
       "-set required 1 100 cycles " .
       "print summary " .

       "-exit > $genDir/cycles.txt 2>&1");

