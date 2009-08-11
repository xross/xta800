#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/error.log 2>&1");
}

system("@ARGV " .
       "load $genDir/test.xe " .
       "analyse function main " .
       "set time - main 100.0 ns " .
       "set time - main 100 cycles " .
       "set time - main 50 mhz " .
       "exit > $genDir/error.txt 2>&1");

