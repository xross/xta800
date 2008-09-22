#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -std=c99 -g test.c -o $genDir/test.xe > $genDir/conditional_expression.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "loop 0 1 10 " .
       "loop 1 2 55 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/conditional_expression.txt 2>&1");

