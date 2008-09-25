#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g a.xc b.xc c.xc -o $genDir/test.xe > $genDir/list_sources.log 2>&1");
system("@ARGV load $genDir/test.xe list sources exit > $genDir/list_sources.txt 2>&1");

