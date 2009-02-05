#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/remove_labels.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add label bob test.xc:5 " .
       "add label bill test.xc:13 " .
       "list label ". 
       "remove label bob bill " .
       "-list label ". 
       "add label bob test.xc:11 " .
       "add label bill test.xc:7 " .
       "list label ". 
       "remove label .* bill " .
       "-list label ". 
       "exit > $genDir/remove_labels.txt 2>&1");

