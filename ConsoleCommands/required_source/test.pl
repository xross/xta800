#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/required_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add required test.xc:6 " .
       "list required " .
       "analyse function test " .
       "summary simple " .
       "print src 2 " .
       "remove required test.xc:6 " .
       "list required " .
       "add required test.xc:10 " .
       "list required " .
       "analyse function test " .
       "summary simple " .
       "print src 1 " .
       "exit > $genDir/required_source.txt 2>&1");
