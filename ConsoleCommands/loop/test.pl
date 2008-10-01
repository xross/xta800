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
       "loopi 0 1 r 5 " .
       "loopi 1 2 r 4 " .
       "loopi 2 3 r 3 " .
       "loopi 3 4 r 2 " .
       "loopi 4 5 r 1 " .
       "summary COMPLETE_LOOPS " .
       "src 0 test.xc " .
       "src 1 test.xc " .
       "src 2 test.xc " .
       "src 3 test.xc " .
       "src 4 test.xc " .
       "src 5 test.xc " .
       "exit > $genDir/loop.txt 2>&1");

