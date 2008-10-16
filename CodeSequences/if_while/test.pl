#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/if_while.log 2>&1");
system("@ARGV load $genDir/test.xe function main summary SIMPLE summary COMPLETE_LOOPS src 4 test.xc src 3 test.xc src 2 test.xc exit > $genDir/if_while.txt 2>&1");

