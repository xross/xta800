#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc test1.xc -o $genDir/test1.xe > $genDir/load_twice.log 2>&1");
system("xcc test2.xc -o $genDir/test2.xe > $genDir/load_twice.log 2>&1");

system("@ARGV load $genDir/test1.xe status load $genDir/test2.xe status exit > $genDir/load_twice.txt 2>&1");

