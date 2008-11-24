#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/config_freq.log 2>&1");
}
system("@ARGV config freq 100 status exit > $genDir/config_freq.txt 2>&1");

