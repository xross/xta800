#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -nostartfiles test.s -o $genDir/test.xe > $genDir/mid_block_endpoints.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path 0x10004 0x10014 " .
       "print completed_paths " .
       "print trace - 0 " .
       "-exit > $genDir/mid_block_endpoints.txt 2>&1");

