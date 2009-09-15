#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $target  = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -target=XS1-G4A-FB512 -nostartfiles test.s -o $genDir/test.xe > $genDir/all_instructions_revb.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .

       "analyse function main " .
       "print trace - " .
       "-exit > $genDir/all_instructions_revb.txt 2>&1");

