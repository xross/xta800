#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;

my $cwd = getcwd();
my $suite = basename($cwd);
my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
        system("xcc -target=$target -march=$arch -g test.c -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function test " .
       "print structure - " .
       "-exit > $genDir/$suite.txt 2>&1");

