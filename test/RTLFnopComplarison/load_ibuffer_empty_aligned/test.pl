#!/usr/bin/perl
use strict;
use warnings;
use File::Basename;
use File::Copy;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
my $cwd = getcwd();
my $suite = basename($cwd);

if (!$noBuild) {
        system("xcc -target=$target -march=$arch -nostartfiles test.S -I ../ -o $genDir/test.xe > $genDir/$suite.log 2>&1");
}

system("@ARGV load $genDir/test.xe analyse function main print trace - -exit > $genDir/$suite.tmp 2>&1");
system("perl ../create_compare_file.pl $genDir $suite");
