#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc test.xc -o $genDir/test.xe > $genDir/status_load.log 2>&1");
system("xta load $genDir/test.xe status exit > $genDir/status_load.txt 2>&1");

