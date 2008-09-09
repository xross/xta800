#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc test.xc -o $genDir/test.xe > $genDir/change_freq.log 2>&1");
system("xta freq 100 status exit > $genDir/change_freq.txt 2>&1");

