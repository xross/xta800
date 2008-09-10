#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test.xc -o $genDir/test.xe > $genDir/pwd.log 2>&1");
system("@ARGV pwd exit > $genDir/pwd.txt 2>&1");

