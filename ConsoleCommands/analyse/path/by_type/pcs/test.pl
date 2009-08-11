#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=$target -march=$arch -nostartfiles -g test.s -o $genDir/test.xe > $genDir/pcs.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "-analyse path 0x10040 0x10080 " .
       "-print structure - " .
       "-analyse path PC:0x10040 PC:0x10080 " .
       "-print structure - " .
       "-exit > $genDir/pcs.txt 2>&1");

