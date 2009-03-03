#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_s(i(a)w(i(a)i(a)i(s(aa)))).xc -o $genDir/test_s(i(a)w(i(a)i(a)i(s(aa)))).xe > $genDir/test_s(i(a)w(i(a)i(a)i(s(aa)))).log 2>&1");
}
system("@ARGV load $genDir/test_s(i(a)w(i(a)i(a)i(s(aa)))).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_s(i(a)w(i(a)i(a)i(s(aa)))).txt 2>&1");

