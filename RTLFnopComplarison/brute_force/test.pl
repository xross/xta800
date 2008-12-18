#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
        system("xcc -march=$arch -nostartfiles test.S -I ../ -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}


system("@ARGV load $genDir/test.xe function main trace 0 exit > $genDir/$suite.txt 2>&1");

