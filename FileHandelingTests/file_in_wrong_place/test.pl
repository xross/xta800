#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("@ARGV load $genDir/test.xe analyse function main exit > $genDir/file_in_wrong_place.log 2>&1");
}

system("touch file_in_wrong_place.txt");
