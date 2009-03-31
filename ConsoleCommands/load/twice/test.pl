#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test1.xc -o $genDir/test1.xe > $genDir/twice.log 2>&1");
system("xcc -march=$arch test2.xc -o $genDir/test2.xe > $genDir/twice.log 2>&1");
}
system("@ARGV load $genDir/test1.xe " .
       "status " .
       "analyse function main " .
       "status " .
       "load $genDir/test2.xe " .
       "status " .
       "analyse function main " .
       "status " .
       "exit > $genDir/twice.txt 2>&1");

