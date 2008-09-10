#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test.xc -o $genDir/test.xe > $genDir/freq.log 2>&1");
system("@ARGV freq 100 status exit > $genDir/freq.txt 2>&1");

