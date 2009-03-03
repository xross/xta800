#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_s(w(i(a)i(a)i(s(aa)))i(i(aa))i(a)ai(a)).xc -o $genDir/test_s(w(i(a)i(a)i(s(aa)))i(i(aa))i(a)ai(a)).xe > $genDir/test_s(w(i(a)i(a)i(s(aa)))i(i(aa))i(a)ai(a)).log 2>&1");
}
system("@ARGV load $genDir/test_s(w(i(a)i(a)i(s(aa)))i(i(aa))i(a)ai(a)).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_s(w(i(a)i(a)i(s(aa)))i(i(aa))i(a)ai(a)).txt 2>&1");

