#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc test.xc -o $genDir/test.xe > $genDir/history.log 2>&1");
system("xta load $genDir/test.xe history exit > $genDir/history.txt 2>&1");

