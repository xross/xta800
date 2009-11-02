#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -g test.xc -o $genDir/test.xe > $genDir/endpoint_endpoint.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "list endpoints " .
       "analyse path output1 input1 " .
       "analyse path output0 input0 " .
       "-config proc 0 1 " .
       "list endpoints " .
       "analyse path output0 input0 " .
       "analyse path output1 input1 " .
       "print summary " .
       "-exit > $genDir/endpoint_endpoint.txt 2>&1");

