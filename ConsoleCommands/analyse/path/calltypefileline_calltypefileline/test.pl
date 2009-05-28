#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -fxta-info test.xc -o $genDir/test.xe > $genDir/calltypefileline_calltypefileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "analyse path CALL:test.xc:13 CALL:test.xc:14 " .
       "analyse path CALL:test.xc:19 CALL:test.xc:20 " .
       "-config proc 0 1 " .
       "analyse path CALL:test.xc:19 CALL:test.xc:20 " .
       "analyse path CALL:test.xc:13 CALL:test.xc:14 " .
       "print summary " .
       "-exit > $genDir/calltypefileline_calltypefileline.txt 2>&1");

