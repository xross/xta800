#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/endpoint_endpoint.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path input1 input2 " .
       "status " .
       "summary simple " .
       "exit > $genDir/endpoint_endpoint.txt 2>&1");

