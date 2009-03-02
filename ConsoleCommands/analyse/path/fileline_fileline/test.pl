#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/fileline_fileline.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path test.xc:9 test.xc:11 " .
       "status " .
       "summary simple " .
       "exit > $genDir/fileline_fileline.txt 2>&1");

