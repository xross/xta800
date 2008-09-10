#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/status_path.log 2>&1");
system("@ARGV load $genDir/test.xe path test.xc:3 test.xc:4 status exit > $genDir/status_path.txt 2>&1");

