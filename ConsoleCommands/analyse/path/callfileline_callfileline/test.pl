#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info test.xc -o $genDir/test.xe > $genDir/callfileline_callfileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "analyse path test.xc:13 test.xc:14 " .
       "analyse path test.xc:19 test.xc:20 " .
       "-config proc 0 1 " .
       "analyse path test.xc:19 test.xc:20 " .
       "analyse path test.xc:13 test.xc:14 " .
       "print summary " .
       "-exit > $genDir/callfileline_callfileline.txt 2>&1");

