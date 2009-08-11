#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc functions.xc -o $genDir/test.xe > $genDir/xta_broken_command.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "print summary " .
       "-exit > $genDir/xta_broken_command.txt 2>&1");

