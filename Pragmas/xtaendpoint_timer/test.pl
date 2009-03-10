#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -g test.xc -o $genDir/test.xe > $genDir/xtaendpoint_timer.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "list endpoints " .
       "list allendpoints " .
       "exit > $genDir/xtaendpoint_timer.txt 2>&1");

