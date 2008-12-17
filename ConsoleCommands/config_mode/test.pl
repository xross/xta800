#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/config_mode.log 2>&1");
}
system("@ARGV config mode RESOLVE status exit > $genDir/config_mode.txt 2>&1");

