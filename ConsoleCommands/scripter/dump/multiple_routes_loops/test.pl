#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/multiple_routes_loops.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "source cmds.txt > $genDir/multiple_routes_loops.txt 2>&1");

