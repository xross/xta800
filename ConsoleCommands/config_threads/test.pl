#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/config_threads.log 2>&1");
system("@ARGV config threads 4 status exit > $genDir/config_threads.txt 2>&1");

