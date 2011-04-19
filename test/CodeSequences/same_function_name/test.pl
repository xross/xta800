#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
    system("xcc -target=$target -march=$arch -g test.xc same.xc -o $genDir/test.xe > $genDir/same_function_name.log 2>&1");
}

system("@ARGV load $genDir/test.xe " .
       "-analyse function f " .
       "-config verbosity 10000 " .
       "-print structure - " .
       "-exit > $genDir/same_function_name.txt 2>&1");

