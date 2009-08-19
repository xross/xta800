#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/num_paths.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function main " .
       "print node_info - - " .
       "exit > $genDir/num_paths.txt 2>&1");

