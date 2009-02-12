#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/load_store.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print src 6 " .
        "print src 5 " .
        "print src 4 " .
        "print src 3 " .
       "exit > $genDir/load_store.txt 2>&1");

