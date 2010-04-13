#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc test.xn -o $genDir/test.xe > $genDir/test.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config freq A 100 " .
       "config freq B 200 " .
       "config freq C 300 " .
       "config freq D 400 " .
       "analyze function f " .
       "status " .
       "-exit > $genDir/test.txt 2>&1");

