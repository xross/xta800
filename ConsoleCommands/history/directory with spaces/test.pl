#!/usr/bin/perl
use strict;
use warnings;
use Cwd;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/test.log 2>&1");
}

my $path = getcwd();
system("@ARGV  " .
       "-load \"$path/$genDir/test.xe\" " .
       "-source \"$path/cmds.txt\" " .
       "-history " .
       "-exit > $genDir/test.txt 2>&1");

