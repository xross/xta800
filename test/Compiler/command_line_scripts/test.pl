#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
  system("xcc cmds.xta -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/command_line_scripts.log 2>&1");
}

my $FILE;
open(FILE, "> $genDir/command_line_scripts.txt");
close(FILE);
