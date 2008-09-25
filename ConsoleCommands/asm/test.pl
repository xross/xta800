#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;
my $arch = $ARGV[0];
shift @ARGV;

system("xcc -march=xs1a -g test.s -o $genDir/test.xe > $genDir/asm.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary SIMPLE " .
       "asm 0 " .
       "asm 1 " .
       "exit > $genDir/asm.txt 2>&1");

