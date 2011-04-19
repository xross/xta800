#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/different_functions.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add exclusion call1,else " .
       "add exclusion call2,if " .
       "analyse function f " .
       "print summary " .
       "-exit > $genDir/different_functions.txt 2>&1");

