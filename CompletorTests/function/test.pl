#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/function.log 2>&1");
}
open (FILE, ">$genDir/cmds.txt");
print FILE "lo\t $genDir/t\t\nan\tfun\tele\t\nprint summary\nexit\n";
close FILE;

system("@ARGV < $genDir/cmds.txt > $genDir/function.txt 2>&1");

