#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -nostartfiles test.s -o $genDir/test.xe > $genDir/mid_block_endpoints.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "analyse path 0x10004 0x10014 " .
       "print trace - " .
       "-exit > $genDir/mid_block_endpoints.txt 2>&1");

