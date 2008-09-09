#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test.xc -o $genDir/test.xe > $genDir/change_threads.log 2>&1");
system("@ARGV threads 4 status exit > $genDir/change_threads.txt 2>&1");

