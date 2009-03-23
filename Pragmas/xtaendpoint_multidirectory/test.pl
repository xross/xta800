#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc test.xc src1/src.xc src2/src.xc -o $genDir/test.xe > $genDir/xtaendpoint_multidirectory.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list endpoints " .
       "list allendpoints " .
       "exit > $genDir/xtaendpoint_multidirectory.txt 2>&1");

