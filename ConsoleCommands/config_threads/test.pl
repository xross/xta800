#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/config_threads.log 2>&1");
}
system("@ARGV config threads 4 status exit > $genDir/config_threads.txt 2>&1");

