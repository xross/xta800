#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/print_lib_coverage.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "function printchar_test " .
       "function printcharln_test " .
       "function printint_test " .
       "function printintln_test " .
       "function printuint_test " .
       "function printuintln_test " .
       "function printhex_test " .
       "function printhexln_test " .
       "function printstr_test " .
       "function printstrln_test " .
       "summary SIMPLE " .
       "exit > $genDir/print_lib_coverage.txt 2>&1");

