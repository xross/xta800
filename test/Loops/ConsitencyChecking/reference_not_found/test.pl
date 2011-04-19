#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/reference_not_found.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function a " .
       "set loop - loop_b 10 " .
       "-exit > $genDir/reference_not_found.txt 2>&1");


