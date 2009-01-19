#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/prohibited_twice.log 2>&1");
}
system("@ARGV load $genDir/test.xe " .
       "add prohibited 0x100d0 " .
       "list prohibited " .
       "add prohibited 0x100d0 " .
       "list prohibited " .
       "exit > $genDir/prohibited_twice.txt 2>&1");
