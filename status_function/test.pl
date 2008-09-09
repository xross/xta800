#!/usr/bin/perl
use strict;
use warnings;
my $genDir = $ARGV[0];
system("xcc test.xc -o $genDir/test.xe > $genDir/status_function.log 2>&1");
system("xta load $genDir/test.xe function main status exit > $genDir/status_function.txt 2>&1");

