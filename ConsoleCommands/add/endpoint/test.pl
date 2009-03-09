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
       "config proc 0 0 " .
       "status " .
       "add endpoint 0x10000 bob " .
       "add endpoint test.xc:11 bill " .
       "add endpoint test.xc:12 ben " .
       "add endpoint test.xc:13 fred " .
       "list endpoints " .
       "config proc 0 0 " .
       "status " .
       "list endpoints " .
       "config proc 0 2 " .
       "status " .
       "list endpoints " .
       "add endpoint 0x10000 bob " .
       "add endpoint test.xc:11 bill1 " .
       "add endpoint test.xc:12 ben1 " .
       "add endpoint test.xc:13 fred1 " .
       "list endpoints " .
       "config proc 0 0 " .
       "status " .
       "list endpoints " .
       "exit > $genDir/endpoint.txt 2>&1");

