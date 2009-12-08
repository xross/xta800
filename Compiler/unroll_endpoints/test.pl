#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  system("xcc cmds.xta -save-temps -target=$target -march=$arch -g -O3 test.xc functions.xc -o $genDir/test.xe > $genDir/unroll_endpoints.log 2>&1");
}

my $FILE;
open(FILE, "> $genDir/unroll_endpoints.txt");
close(FILE);
