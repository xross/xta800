#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g fft.xc -o $genDir/test.xe > $genDir/fft.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function fix_fft_64 " .
       "summary SIMPLE " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/fft.txt 2>&1");

