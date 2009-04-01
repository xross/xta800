#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/while_in_do_while2.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print summary " .
       "print completed_loops " .
       "set loopmax - do 100 " .
       "set loopmax - do 50 " .
       "print completed_loops " .
       "exit > $genDir/while_in_do_while2.txt 2>&1");

