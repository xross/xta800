#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/code_reference.log 2>&1");
}
open (FILE, ">$genDir/cmds.txt");
print FILE "load $genDir/test.xe\nadd e\t\tmem\t\nexit\n";
close FILE;

system("@ARGV < $genDir/cmds.txt > $genDir/code_reference.txt 2>&1");

