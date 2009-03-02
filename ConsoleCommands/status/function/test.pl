#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/function.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "status " .
       "exit > $genDir/function.txt 2>&1");

