#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "loopi 1 1 r 10 " .
       "loopi 0 2 a 55 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/dependent_nested_loop.txt 2>&1");

