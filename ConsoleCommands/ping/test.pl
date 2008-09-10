#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test.xc -o $genDir/test.xe > $genDir/ping.log 2>&1");
system("@ARGV status source cmds.txt > $genDir/ping.txt 2>&1");

