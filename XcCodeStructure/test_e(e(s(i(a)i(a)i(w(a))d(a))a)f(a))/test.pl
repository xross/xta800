#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_e(e(s(i(a)i(a)i(w(a))d(a))a)f(a)).xc -o $genDir/test_e(e(s(i(a)i(a)i(w(a))d(a))a)f(a)).xe > $genDir/test_e(e(s(i(a)i(a)i(w(a))d(a))a)f(a)).log 2>&1");
}
system("@ARGV load $genDir/test_e(e(s(i(a)i(a)i(w(a))d(a))a)f(a)).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_e(e(s(i(a)i(a)i(w(a))d(a))a)f(a)).txt 2>&1");

