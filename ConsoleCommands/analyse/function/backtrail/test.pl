#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/backtrail.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function a,f " .
       "analyse function b,f " .
       "print summary " .
       "exit > $genDir/backtrail.txt 2>&1");

