#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/cores.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-status " .
       "list cores " .
       "-remove tileref tile[0] " .
       "-remove tileref tile[1] " .
       "-remove tileref tile[2] " .
       "-remove tileref tile[3] " .
       "-status " .
       "list cores " .
       "-add tileref tile[0] " .
       "-add tileref tile[1] " .
       "-add tileref tile[2] " .
       "-add tileref tile[3] " .
       "-status " .
       "list cores " .
       "exit > $genDir/cores.txt 2>&1");

