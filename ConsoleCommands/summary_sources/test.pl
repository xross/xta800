#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g main.xc test1.xc test2.xc test3.xc test4.xc test5.xc -o $genDir/test.xe > $genDir/summary_sources.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SOURCES " .
       "exit > $genDir/summary_sources.txt 2>&1");

