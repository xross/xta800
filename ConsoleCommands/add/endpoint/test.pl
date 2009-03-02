#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/endpoint.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add endpoint 0x10000 bob " .
       "add endpoint test.xc:9 bill " .
       "add endpoint test.xc:10 ben " .
       "add endpoint test.xc:11 fred " .
       "list endpoints " .
       "exit > $genDir/endpoint.txt 2>&1");

