#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.s -o $genDir/test.xe > $genDir/no_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "status " .
       "list sources " .
       "exit > $genDir/no_source.txt 2>&1");

