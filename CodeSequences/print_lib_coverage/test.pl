#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g -Xmapper --retain test.xc -o $genDir/test.xe > $genDir/print_lib_coverage.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function printchar_test " .
       "summary simple " .
       "analyse function printcharln_test " .
       "summary simple " .
       "analyse function printint_test " .
       "summary simple " .
       "analyse function printintln_test " .
       "summary simple " .
       "analyse function printuint_test " .
       "summary simple " .
       "analyse function printuintln_test " .
       "summary simple " .
       "analyse function printhex_test " .
       "summary simple " .
       "analyse function printhexln_test " .
       "summary simple " .
       "analyse function printstr_test " .
       "summary simple " .
       "analyse function printstrln_test " .
       "summary simple " .
       "exit > $genDir/print_lib_coverage.txt 2>&1");

