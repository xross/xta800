#!/usr/bin/perl
use strict;
use warnings;

my $genDir  = shift(@ARGV);
my $arch    = shift(@ARGV);
my $noBuild = shift(@ARGV);

if (!$noBuild) {
system("xcc -march=$arch -g test.xc -o $genDir/test.xe > $genDir/basic.log 2>&1");
}
system("@ARGV " .
       "set constraint 0x100cc " .
       "-list constraint " .
       "-load $genDir/test.xe " .
       "source cmds.txt > $genDir/basic.txt 2>&1");
