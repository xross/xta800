#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -nostartfiles test.s -o $genDir/test.xe > $genDir/addative_paths.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path A B " .
       "summary complete " .
       "print trace 0 " .
       "analyse path B C " .
       "summary complete " .
       "print trace 0 " .
       "analyse path A C " .
       "summary complete " .
       "print trace 0 " .
       "-exit > $genDir/addative_paths.txt 2>&1");

