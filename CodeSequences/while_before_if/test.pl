#!/usr/bin/perl
use strict;
use warnings;

my $genDir = $ARGV[0];
shift @ARGV;

system("xcc -g test.xc -o $genDir/test.xe > $genDir/while_before_if.log 2>&1");
system("@ARGV load $genDir/test.xe " .
       "function main " .
       "summary COMPLETE_LOOPS " .
       "exit > $genDir/while_before_if.txt 2>&1");

