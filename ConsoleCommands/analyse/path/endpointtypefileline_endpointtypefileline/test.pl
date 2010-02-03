#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch test.xc -o $genDir/test.xe > $genDir/endpointtypefileline_endpointtypefileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config core 0 0 " .
       "-analyse path ENDPOINT:test.xc:16 ENDPOINT:test.xc:17 " .
       "analyse path ENDPOINT:test.xc:7 ENDPOINT:test.xc:8 " .
       "-config core 0 1 " .
       "-analyse path ENDPOINT:test.xc:7 ENDPOINT:test.xc:8 " .
       "analyse path ENDPOINT:test.xc:16 ENDPOINT:test.xc:17 " .
       "print summary " .
       "-exit > $genDir/endpointtypefileline_endpointtypefileline.txt 2>&1");

