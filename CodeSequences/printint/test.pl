#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/printint.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function printint " .
       "summary SIMPLE " .
       "exit > $genDir/printint.txt 2>&1");

