#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/dependent_nested_loop_fcall.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "summary COMPLETE_LOOPS " .
       "loopi 13 r 10 " .
       "loopi 5 a 55 " .
       "loopi 11 r 1 " .
       "loopi 3 a 1100 " .
       "loopi 9 r 20 " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/dependent_nested_loop_fcall.txt 2>&1");

