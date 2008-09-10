#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test.xc -o $genDir/test.xe > $genDir/help.log 2>&1");
system("@ARGV help exit > $genDir/help.txt 2>&1");

