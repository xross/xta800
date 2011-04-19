#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);
if (!$noBuild) {
        system("xcc -march=$arch -g test.xc 4XVB.xn -o $genDir/test.xe > $genDir/multi_node.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse function f " .
       "print nodeinfo - - " .
       "-exit > $genDir/multi_node.txt 2>&1");

