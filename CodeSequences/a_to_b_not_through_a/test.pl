#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
        system("xcc -target=$target -march=$arch -std=c99 test.xc -g -o $genDir/test.xe > $genDir/a_to_b_not_through_a.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "-add exclusion foo " .
       "-add exclusion end " .
       "-analyse path foo bar " .
       "-print trace - " .
       "-exit > $genDir/a_to_b_not_through_a.txt 2>&1");

