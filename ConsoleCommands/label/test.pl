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
       "add label 0x1019a bob " .
       "add label bob bill " .
       "list labels ". 
       "remove label fred " .
       "-list labels ". 
       "remove label bob " .
       "-list labels ". 
       "remove label bill " .
       "-list labels ". 
       "exit > $genDir/label.txt 2>&1");

