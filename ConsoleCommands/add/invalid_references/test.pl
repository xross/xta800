#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/invalid_references.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-add branch 100 100 " .
       "-add label 99 silly " .
       "-remove branch 100 " .
       "-remove label silly " .
       "-add label 0x10000 silly " .
       "-add label 0x100c6 silly " .
       "-add label 0x10000 0x1ab1e " .
       "exit > $genDir/invalid_references.txt 2>&1");

