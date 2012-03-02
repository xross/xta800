#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -O1 -march=$arch test.xc extern.xc -o $genDir/test.xe > $genDir/while.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "print structure - " .
       "-exit > $genDir/while.txt 2>&1");

