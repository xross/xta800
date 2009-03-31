#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g a.xc b.xc c.xc -o $genDir/test.xe > $genDir/functions.log 2>&1");
}
system("@ARGV load $genDir/test.xe " . 
       "list functions " .
       "config proc 0 1 " .
       "list functions " .
       "config proc 0 3 " .
       "list functions " .
       "config proc 0 2 " .
       "list functions " .
       "exit > $genDir/functions.txt 2>&1");

