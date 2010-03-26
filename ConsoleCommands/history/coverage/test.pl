#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/coverage.log 2>&1");
}
open (FILE, ">$genDir/cmds.txt");
print FILE "load $genDir/test.xe\nan\tpa\t\nhistory\nexit\n";
close FILE;

system("@ARGV < $genDir/cmds.txt > $genDir/coverage.txt 2>&1");
