#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -g -march=$arch -fxta-info test.xc -o $genDir/test.xe > $genDir/fileline_fileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "config proc 0 0 " .
       "analyse path test.xc:16 test.xc:17 " .
       "status " .
       "analyse path test.xc:7 test.xc:8 " .
       "status " .
       "print summary " .
       "config proc 0 1 " .
       "analyse path test.xc:7 test.xc:8 " .
       "status " .
       "analyse path test.xc:16 test.xc:17 " .
       "status " .
       "print summary " .
       "exit > $genDir/fileline_fileline.txt 2>&1");

