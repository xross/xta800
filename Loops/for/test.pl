#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/for.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "exit > $genDir/for.txt 2>&1");

