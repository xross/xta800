#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch test.xc -o $genDir/test.xe > $genDir/invalid_references.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add branch 100 100 " .
       "add label silly 99 " .
       "add prohibited 98 " .
       "add required 97 " .
       "add filter 96 " .
       "remove branch 100 " .
       "remove label silly " .
       "-remove prohibited 98 " .
       "remove required 97 " .
       "remove filter 96 " .
       "add label silly 0x100c4 " .
       "add label silly 0x100c6 " .
       "add prohibited 0x100c4 " .
       "add prohibited 0x100c4 " .
       "add required 0x100c4 " .
       "add required 0x100c4 " .
       "add filter 0x100c4 " .
       "add filter 0x100c4 " .
       "exit > $genDir/invalid_references.txt 2>&1");

