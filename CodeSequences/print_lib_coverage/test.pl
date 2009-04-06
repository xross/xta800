#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g -Xmapper --retain test.xc -o $genDir/test.xe > $genDir/print_lib_coverage.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function printchar_test " .
       "analyse function printcharln_test " .
       "analyse function printint_test " .
       "analyse function printintln_test " .
       "analyse function printuint_test " .
       "analyse function printuintln_test " .
       "analyse function printhex_test " .
       "analyse function printhexln_test " .
       "analyse function printstr_test " .
       "analyse function printstrln_test " .
       "print summary " .
       "exit > $genDir/print_lib_coverage.txt 2>&1");

