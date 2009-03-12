#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc add/add.xc sub/sub.xc -o $genDir/test.xe > $genDir/multi_directory.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list sources " .
       "analyse function main " .
       "print src - 0 " .
       "print src - 1 " .
       "exit  > $genDir/multi_directory.txt 2>&1");

