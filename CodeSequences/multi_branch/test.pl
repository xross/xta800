#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -nostartfiles test.s -o $genDir/test.xe > $genDir/multi_branch.log 2>&1");
system("@ARGV load $genDir/test.xe function main summary SIMPLE exit > $genDir/multi_branch.txt 2>&1");

