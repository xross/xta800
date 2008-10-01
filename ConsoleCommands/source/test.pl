#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/source.log 2>&1");
system("@ARGV source cmds.txt > $genDir/source.txt 2>&1");

