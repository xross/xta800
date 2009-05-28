#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info test.xc -o $genDir/test.xe > $genDir/endpointfileline_endpointfileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "analyse path test.xc:16 test.xc:17 " .
       "analyse path test.xc:7 test.xc:8 " .
       "-config proc 0 1 " .
       "analyse path test.xc:7 test.xc:8 " .
       "analyse path test.xc:16 test.xc:17 " .
       "print summary " .
       "-exit > $genDir/endpointfileline_endpointfileline.txt 2>&1");

