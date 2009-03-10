#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("cp a.xc test.xc");
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/file_in_wrong_place.log 2>&1");
system("mv test.xc ./up/test.xc");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "-exit > $genDir/file_in_wrong_place.txt 2>&1");
