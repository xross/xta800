#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/exclude.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "exclude test.xc:11 " . 
       "function main " .
       "summary SIMPLE " .
       "src 1 test.xc " .
       "exit > $genDir/exclude.txt 2>&1");

