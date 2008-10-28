#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;
system("xcc -march=$arch -S -g test.xc -o  $genDir/test.s > $genDir/multi_path_in_called_function2.log 2>&1 ");
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/multi_path_in_called_function2.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
        "summary SIMPLE " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/multi_path_in_called_function2.txt 2>&1");

