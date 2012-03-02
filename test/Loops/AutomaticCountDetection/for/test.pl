#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -O1 -target=$target -march=$arch test.xc extern.xc -o $genDir/test.xe > $genDir/for.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "print structure - " .
       "-exit > $genDir/for.txt 2>&1");

