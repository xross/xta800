#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
  system("xcc -march=$arch -g test_w(w(a))w(aaa).xc -o $genDir/test_w(w(a))w(aaa).xe > $genDir/test_w(w(a))w(aaa).log 2>&1");
}
system("@ARGV load $genDir/test_w(w(a))w(aaa).xe " .
       "analyse function test " .
       "summary complete_loops " .
       "exit > $genDir/test_w(w(a))w(aaa).txt 2>&1");

