#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/invalid.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 3 " .
       "analyse function test0 " .
       "analyse function test1 " .
       "analyse function test2 " .
       "analyse function test3 " .
       "config proc 0 2 " .
       "analyse function test0 " .
       "analyse function test1 " .
       "analyse function test2 " .
       "analyse function test3 " .
       "config proc 0 1 " .
       "analyse function test0 " .
       "analyse function test1 " .
       "analyse function test2 " .
       "analyse function test3 " .
       "config proc 0 0 " .
       "analyse function test0 " .
       "analyse function test1 " .
       "analyse function test2 " .
       "analyse function test3 " .
       "status " .
       "exit > $genDir/invalid.txt 2>&1");

