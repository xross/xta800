#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch -g test.xc same.xc -o $genDir/test.xe > $genDir/same_function_name_with_recursion_test.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "analyse function same " .
       "print structure - " .
       "exit > $genDir/same_function_name_with_recursion_test.txt 2>&1");

