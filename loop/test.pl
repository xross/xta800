#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/loop.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "loop 0 1 5 " .
       "loop 1 2 4 " .
       "loop 2 3 3 " .
       "loop 3 4 2 " .
       "loop 4 5 1 " .
       "summary COMPLETE_LOOPS " .
       "src 0 test.xc " .
       "src 1 test.xc " .
       "src 2 test.xc " .
       "src 3 test.xc " .
       "src 4 test.xc " .
       "src 5 test.xc " .
       "exit > $genDir/loop.txt 2>&1");

