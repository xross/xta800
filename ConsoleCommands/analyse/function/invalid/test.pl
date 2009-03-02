#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/invalid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function function_that_doesnt_exist " .
       "status " .
       "exit > $genDir/invalid.txt 2>&1");

