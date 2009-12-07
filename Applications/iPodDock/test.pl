#!/usr/bin/perl
use strict;
use warnings;
use File::Copy;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $directory = "iPodDock";

if (!$noBuild) {
    system(" xmake -f Makefile clean     > $genDir/$directory.log 2>&1");
    system(" cp ./bin/.not_a_test ./bin/XC-5 2>&1");
    system(" cp ./bin/.not_a_test ./build/XC-5 2>&1");
    system(" xmake -f Makefile all       > $genDir/$directory.log 2>&1");
}

open(FILE, ">$genDir/$directory.txt");
close(FILE);