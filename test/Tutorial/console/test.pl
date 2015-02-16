#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target mii.xc -O3 -g -o $genDir/tutorial_console.xe > $genDir/console.log 2>&1");
}
system("@ARGV load $genDir/tutorial_console.xe " .
       "source receive.sr.sample > $genDir/console.txt 2>&1");
