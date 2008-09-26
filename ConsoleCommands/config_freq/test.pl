#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/config_freq.log 2>&1");
system("@ARGV config freq 100 status exit > $genDir/config_freq.txt 2>&1");

