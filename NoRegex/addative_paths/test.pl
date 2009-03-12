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
       "analyse path B C " .
       "analyse path A C " .

       "print completed_paths " .
       "echo ========================= " .
       "print trace 0 0 " .
       "echo ========================= " .
       "print trace 1 0 " .
       "echo ========================= " .
       "print trace 2 0 " .

       "-exit > $genDir/addative_paths.txt 2>&1");

