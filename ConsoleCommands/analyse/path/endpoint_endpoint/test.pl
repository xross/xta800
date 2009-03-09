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
       "config proc 0 0 " .
# BUG 4812 - uncalled elimination bug
#       "list endpoints " .
#       "analyse path output1 input1 " .
#       "status " .
       "analyse path output0 input0 " .
       "status " .
       "summary simple " .
       "config proc 0 1 " .
#       "list endpoints " .
       "analyse path output1 input1 " .
       "status " .
#       "analyse path output0 input0 " .
#       "status " .
       "summary simple " .
       "exit > $genDir/endpoint_endpoint.txt 2>&1");

