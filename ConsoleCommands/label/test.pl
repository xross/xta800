#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/label.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add label bob 0x1019a " .
       "add label bill bob " .
       "list label ". 
       "remove label fred " .
       "list label ". 
       "remove label bob " .
       "list label ". 
       "remove label bill " .
       "list label ". 
       "exit > $genDir/label.txt 2>&1");

