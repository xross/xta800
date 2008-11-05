#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/reverse_ordered_select.log 2>&1");
system("@ARGV load $genDir/test.xe path 0x101a2 0x10192 summary SIMPLE summary COMPLETE summary COMPLETE_LOOPS summary COMPLETE_INSTRUCTIONS exit > $genDir/reverse_ordered_select.txt 2>&1");

