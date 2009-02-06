#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/prohibited_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add prohibited test.xc:6 " .
       "list prohibited " .
       "analyse function test " .
       "summary simple " .
       "print src 1 " .
       "remove prohibited test.xc:6 " .
       "list prohibited " .
       "add prohibited test.xc:10 " .
       "list prohibited " .
       "analyse function test " .
       "summary simple " .
       "print src 2 " .
       "exit > $genDir/prohibited_source.txt 2>&1");
