#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/loop.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "loopi 1 r 5 " .
       "loopi 3 r 4 " .
       "loopi 5 r 3 " .
       "loopi 7 r 2 " .
       "loopi 9 r 1 " .
       "summary COMPLETE_LOOPS " .
       "src 10 test.xc " .
       "src 1 test.xc " .
       "src 3 test.xc " .
       "src 5 test.xc " .
       "src 7 test.xc " .
       "src 9 test.xc " .
       "exit > $genDir/loop.txt 2>&1");

