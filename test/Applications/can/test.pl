#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g -O3 CanCFunctions.c CanFunctions.xc CanPhy.xc CanTest.xc -o $genDir/test.xe > $genDir/can.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "-exit " .
       "> $genDir/can.txt 2>&1");

