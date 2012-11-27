#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  system("xcc -target=$target -march=$arch -g -O3 test.xc -o $genDir/test.xe > $genDir/inline_endpoints.log 2>&1");
}

system("xta -load $genDir/test.xe -source cmds.xta > $genDir/inline_endpoints.txt 2>&1");
