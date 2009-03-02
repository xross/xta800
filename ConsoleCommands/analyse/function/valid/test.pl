#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/valid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "status " .
       "summary simple " .
       "exit > $genDir/valid.txt 2>&1");

