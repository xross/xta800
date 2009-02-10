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
       "add label test.xc:5 bob " .
       "add label test.xc:13 bill " .
       "list labels ". 
       "remove label bob bill " .
       "-list labels ". 
       "add label test.xc:11 bob " .
       "add label test.xc:7 bill " .
       "list labels ". 
       "remove label .* bill " .
       "-list labels ". 
       "exit > $genDir/remove_labels.txt 2>&1");

