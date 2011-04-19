#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g -O3 iic.xc main.xc -o $genDir/test.xe > $genDir/swc_i2c.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "source cmds.txt " .
       "> $genDir/swc_i2c.txt 2>&1");

