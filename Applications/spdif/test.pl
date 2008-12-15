#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "spdif";

if (!$noBuild) {
        system("xcc -nostdlib t.S -o $genDir/test.xe >  $genDir/$directory.log 2>&1");
}

system("xta load $genDir/test.xe exit > $genDir/$directory.txt 2>&1");
