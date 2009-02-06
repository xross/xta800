#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/excluded_source.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add filter test.xc:6 " .
       "list filter " .
       "analyse function test " .
       "summary simple " .
       "print src 1 " .
       "remove filter test.xc:6 " .
       "list filter " .
       "add filter test.xc:10 " .
       "list filter " .
       "analyse function test " .
       "summary simple " .
       "print src 2 " .
       "exit > $genDir/excluded_source.txt 2>&1");
