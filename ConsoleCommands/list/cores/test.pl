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
       "-remove core 0 0 " .
       "-remove core 0 1 " .
       "-remove core 0 2 " .
       "-remove core 0 3 " .
       "-status " .
       "list cores " .
       "-add core stdcore[0] " .
       "-add core stdcore[1] " .
       "-add core stdcore[2] " .
       "-add core stdcore[3] " .
       "-status " .
       "list cores " .
       "exit > $genDir/cores.txt 2>&1");

