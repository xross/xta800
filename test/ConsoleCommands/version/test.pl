#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/version.log 2>&1");
}
system("@ARGV version exit > $genDir/version.txt 2>&1");

