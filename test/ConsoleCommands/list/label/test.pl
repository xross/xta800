#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/label.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list labels " .
       "-exit > $genDir/label.txt 2>&1");

